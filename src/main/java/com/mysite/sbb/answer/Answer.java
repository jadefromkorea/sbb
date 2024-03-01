package com.mysite.sbb.answer;

import java.time.LocalDateTime;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
//    @JoinColumn(name = "qId")
    private Question question;

    // 사용자 한 명이 질문을 여러 개 작성할 수 있기 때문
    @ManyToOne
    private SiteUser author;

//    public void setQuestion(Question question) {
//        if(this.question != null) {
//            this.question.getAnswerList().remove(this);
//        }
//
//        this.question = question;
//        question.getAnswerList().add(this);
//    }
}
