package com.entry.entrydsm.document.domain;

import com.entry.entrydsm.common.domain.BaseTimeEntity;
import com.entry.entrydsm.document.dto.DocumentDTO;
import com.entry.entrydsm.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@NoArgsConstructor
public class Document extends BaseTimeEntity {
    @Id
    @Column(length = 32)
    @JsonIgnore
    private String userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(length = 1600, nullable = false)
    @NotBlank(message = "자기소개서는 비어있을 수 없습니다.")
    private String introduce;

    @Column(length = 1600, nullable = false)
    @NotBlank(message = "학업소개서는 비어있을 수 없습니다.")
    private String studyPlan;

    public Document(User user) {
        this.user = user;
        this.introduce = "";
        this.studyPlan = "";
    }

    public void update(DocumentDTO document) {
        this.introduce = document.getIntroduce();
        this.studyPlan = document.getStudyPlan();
    }
}
