package com.entry.entrydsm.user;

import com.entry.entrydsm.graduate.info.GraduateInfo;
import com.entry.entrydsm.info.Info;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "user_id", length = 32)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private GraduateType graduateType;

    // TODO
//    @OneToOne(mappedBy = "user_id", cascade = CascadeType.ALL)
//    private Info info;
//
//    @OneToOne(mappedBy = "user_id", cascade = CascadeType.ALL)
//    private GraduateInfo graduateInfo;

    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }
}
