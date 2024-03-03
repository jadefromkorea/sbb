package com.mysite.sbb.comment;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private SiteUser author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Answer answer;

    /*
    댓글을 수정하거나 삭제한 후에 질문 상세 페이지로 리다이렉트 하기 위해서는 댓글을 통해 질문의 id를 알아내는 getQuestionId 메서드가 필요하다.
    이후 진행할 댓글 수정, 삭제에서 필요한 기능이지만 편의를 위해 여기서 먼저 만들고 가도록 하자.
    getQuestionId 메서드는 댓글을 통해 질문의 id 값을 리턴하는 메서드로 question 속성이 null이 아닌 경우는
    질문에 달린 댓글이므로 this.question.getId() 값을 리턴하고 답변에 달린 댓글인 경우 this.answer.getQuestion().getId() 값을 리턴하다.
     */
    public Integer getQuestionId() {
        Integer result = null;

        if (this.question != null) {
            result = this.question.getId();
        } else if (this.answer != null) {
            result = this.answer.getQuestion().getId();
        }

        return result;
    }
}
