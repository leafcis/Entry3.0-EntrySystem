package com.entry.entrydsm.user.domain;

import com.entry.entrydsm.apply.domain.ApplyStatus;
import com.entry.entrydsm.common.domain.BaseTimeEntity;
import com.entry.entrydsm.document.domain.Document;
import com.entry.entrydsm.grade.domain.ged.GedScore;
import com.entry.entrydsm.grade.domain.graduate.GradeInfoId;
import com.entry.entrydsm.grade.domain.graduate.GraduateGrade;
import com.entry.entrydsm.grade.domain.graduate.GraduateGradeComparator;
import com.entry.entrydsm.grade.domain.graduate.GraduateScore;
import com.entry.entrydsm.info.domain.Admission;
import com.entry.entrydsm.info.domain.AdmissionDetail;
import com.entry.entrydsm.info.domain.Info;
import com.entry.entrydsm.info.domain.graduate.GraduateInfo;
import com.entry.entrydsm.info.dto.ClassificationDTO;
import com.entry.entrydsm.user.domain.tempuser.TempUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SortComparator;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "user_id", length = 32)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(unique = true)
    private String passwordResetCode;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private GraduateType graduateType;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Admission admission;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AdmissionDetail admissionDetail;

    @Setter
    @Column(nullable = false)
    private Boolean region;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AdditionalType additionalType;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
    private Info info;


    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private GraduateScore graduateScore;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private GedScore gedScore;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
    private ApplyStatus applyStatus;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private Document document;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
    private GraduateInfo graduateInfo;

    @JsonIgnore
    @OneToMany(mappedBy = "id.user", cascade = CascadeType.ALL)
    @SortComparator(GraduateGradeComparator.class)
    private List<GraduateGrade> grades;


    public User(TempUser tempUser) {
        this(tempUser.getEmail(), tempUser.getPassword(), GraduateType.WILL, Admission.NORMAL,
                AdmissionDetail.NONE, false, AdditionalType.NONE);
    }

    @Builder
    public User(String email, String password, GraduateType graduateType, Admission admission, AdmissionDetail admissionDetail, Boolean region, AdditionalType additionalType) {
        this.email = email;
        this.password = password;
        this.graduateType = (graduateType == null) ? GraduateType.WILL : graduateType;
        this.region = (region == null) ? false : region;
        this.admission = (admission == null) ? Admission.NORMAL : admission;
        this.admissionDetail = (this.admission == Admission.SOCIAL && admissionDetail != null) ? admissionDetail : AdmissionDetail.NONE;
        this.additionalType = (additionalType == null) ? AdditionalType.NONE : additionalType;
        initialize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", graduateType=" + graduateType +
                '}';
    }

    public boolean matchPassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.password);
    }

    public void updateInfo(ClassificationDTO classificationDTO) {
        this.graduateType = classificationDTO.getGraduateType();
        this.region = classificationDTO.getRegion();
        this.admission = classificationDTO.getAdmission();
        this.admissionDetail = classificationDTO.getAdmissionDetail();
        if (classificationDTO.getGraduateType() != GraduateType.GED) {
            updateGraduateClassification(classificationDTO);
        }
    }

    private void initialize() {
        this.info = new Info(this);
        this.graduateInfo = new GraduateInfo(this);
        this.grades = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            this.grades.add(GraduateGrade.builder().id(new GradeInfoId(this, i)).build());
        }
        this.graduateScore = new GraduateScore(this);
        this.gedScore = new GedScore(this);
        this.document = new Document(this);
        this.applyStatus = new ApplyStatus(this);
    }

    @AssertTrue(message = "사회통합전형 세부 내용을 선택하지 않으셨습니다.")
    private boolean isValidAdmissionDetail() {
        return (admission.isSocial()) == (!admissionDetail.isNone());
    }

    public void updateGraduateClassification(ClassificationDTO dto) {
        this.graduateInfo.updateClassification(dto);
    }

    public boolean isSubmitted() {
        if (this.applyStatus == null) return false;
        return applyStatus.getFinalSubmit();
    }

    public void submit() {
        if (this.applyStatus == null) return;
        this.applyStatus.submit();
    }

    public boolean matchPasswordResetCode(String passwordResetCode) {
        if (this.passwordResetCode == null) return false;
        return this.passwordResetCode.equals(passwordResetCode);
    }

    public String generatePasswordResetCode() {
        this.passwordResetCode = RandomStringUtils.randomAlphanumeric(5);
        return this.passwordResetCode;
    }

    public void resetPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void clearPasswordResetCode() {
        this.passwordResetCode = null;
    }
}
