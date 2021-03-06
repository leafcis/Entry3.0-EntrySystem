package com.entry.entrydsm.user.service;

import com.entry.entrydsm.common.exception.BadRequestException;
import com.entry.entrydsm.common.exception.ConflictException;
import com.entry.entrydsm.common.exception.UnauthorizedException;
import com.entry.entrydsm.common.response.JwtToken;
import com.entry.entrydsm.common.security.jwt.Jwt;
import com.entry.entrydsm.mail.EmailService;
import com.entry.entrydsm.user.domain.User;
import com.entry.entrydsm.user.domain.UserRepository;
import com.entry.entrydsm.user.domain.tempuser.TempUser;
import com.entry.entrydsm.user.domain.tempuser.TempUserRepository;
import com.entry.entrydsm.user.dto.PasswordResetDTO;
import com.entry.entrydsm.user.dto.SigninDTO;
import com.entry.entrydsm.user.dto.SignupDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import javax.mail.SendFailedException;
import javax.validation.Valid;
import java.util.Optional;

@Service("auth-service")
@Slf4j
public class RealAuthService implements AuthService {

    @Autowired
    private TempUserRepository tempUserRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Jwt jwt;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.prefix}")
    private String prefix;

    @PostConstruct
    private void init() {
        this.prefix += " ";
    }

    @Transactional
    public TempUser signup(@Valid @RequestBody SignupDTO dto) throws SendFailedException {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("email", "이메일이 중복되었습니다.");
        }
        tempUserRepository.deleteByEmail(dto.getEmail());
        tempUserRepository.flush();

        TempUser tempUser = tempUserRepository.save(dto.toTempUser(passwordEncoder));
        emailService.sendConfirmCode(dto.getEmail(), tempUser);

        return tempUser;
    }

    @Transactional
    public JwtToken confirmSignup(String code) throws Exception {
        TempUser tempUser = tempUserRepository.findById(code).orElseThrow(() -> new BadRequestException("올바르지 않은 인증 코드입니다."));
        User user = new User(tempUser);
        tempUserRepository.delete(tempUser);
        return jwt.createToken(userRepository.save(user));
    }

    public Optional<User> validateToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(prefix)) {
            log.debug("Token Validation Failed. authorizationHeader: {}", authorizationHeader);
            return Optional.empty();
        }
        try {
            String token = authorizationHeader.substring(prefix.length());
            log.debug("After Substring Token : {}", token);
            String userId = jwt.getUserId(token);
            log.debug("After Verifying. userId : {}", userId);
            Optional<User> user = userRepository.findById(userId);
            log.debug("User present : {}", user.isPresent());
            return user;
        } catch (Exception e) {
            log.debug("Exception has occurred. {}", e);
            return Optional.empty();
        }
    }

    public JwtToken signin(SigninDTO dto) throws Exception {
        return jwt.createToken(userRepository.findByEmail(dto.getEmail())
                .filter(user -> user.matchPassword(dto.getPassword(), passwordEncoder))
                .orElseThrow(UnauthorizedException::new));
    }

    @Override
    @Transactional
    public void sendPasswordResetCode(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(BadRequestException::new);
        emailService.sendPasswordResetCode(email, user.generatePasswordResetCode());
    }

    @Override
    public void confirmPasswordResetCode(String email, String passwordResetCode) {
        User user = userRepository.findByEmail(email).orElseThrow(BadRequestException::new);
        if (!user.matchPasswordResetCode(passwordResetCode)) {
            throw new BadRequestException();
        }
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(BadRequestException::new);
        if (!user.matchPasswordResetCode(dto.getPasswordResetCode())) {
            throw new BadRequestException();
        }
        user.resetPassword(dto.getPassword(), passwordEncoder);
        user.clearPasswordResetCode();
    }
}
