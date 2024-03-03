package com.mysite.sbb.answer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.sbb.comment.Comment;
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

    private LocalDateTime modifyDate;

    // 사용자 한 명이 질문을 여러 개 작성할 수 있기 때문
    @ManyToOne
    private SiteUser author;

    /*
    @ManyToMany 애너테이션과 함께 Set<SiteUser> voter를 작성해 voter 속성을 다대다 관계로 설정하여 질문 엔티티에 추가했다.
    이때 다른 속성과 달리 Set 자료형으로 작성한 이유는 voter 속성값이 서로 중복되지 않도록 하기 위해서이다.
    List 자료형과 달리 여기서는 Set 자료형이 voter 속성을 관리하는데 효율적이다.

    author 속성을 추가할 때와 달리 QUESTION_VOTER, ANSWER_VOTER라는 테이블이 생성된 것을 확인할 수 있다.
    이렇게 @ManyToMany 애너테이션을 사용해 다대다 관계로 속성을 생성하면 새로운 테이블을 만들어 관련 데이터를 관리한다.
    여기서 생성된 테이블의 인덱스 항목을 펼쳐 보면 서로 연관된 엔티티의 고유 번호(즉, ID)가 기본키로 설정되어 다대다 관계임을 알 수 있다.
     */
    @ManyToMany
    Set<SiteUser> voter;

    @OneToMany(mappedBy = "answer")
    private List<Comment> commentList;

//    public void setQuestion(Question question) {
//        if(this.question != null) {
//            this.question.getAnswerList().remove(this);
//        }
//
//        this.question = question;
//        question.getAnswerList().add(this);
//    }
}
