package com.mysite.sbb.answer;

import java.time.LocalDateTime;

import com.mysite.sbb.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Aid;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "qId")
    private Question question;

    public void setQuestion(Question question) {
        if(this.question != null) {
            this.question.getAnswerList().remove(this);
        }

        this.question = question;
        question.getAnswerList().add(this);
    }
}
