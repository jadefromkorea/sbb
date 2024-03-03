package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getList_old() {
        return this.questionRepository.findAll();
    }

//    getList 메서드는 정수 타입의 페이지 번호를 입력받아 해당 페이지의 Page 객체를 리턴하도록 변경했다.
//    Pageable 객체를 생성할 때 사용한 PageRequest.of(page, 10)에서 page는 조회할 페이지의 번호이고 10은 한 페이지에 보여 줄 게시물의 개수를 의미한다.
//    이렇게 하면 데이터 전체를 조회하지 않고 해당 페이지의 데이터만 조회하도록 쿼리가 변경된다.
//
//    게시물을 역순(최신순)으로 조회하려면 이와 같이 PageRequest.of 메서드의 세 번째 매개변수에 Sort 객체를 전달해야 한다.
//    작성 일시(createDate)를 역순(Desc)으로 조회하려면 Sort.Order.desc("createDate")와 같이 작성한다.
//    만약 작성 일시 외에 정렬 조건을 추가하고 싶다면 sort.add 메서드를 활용해 sorts 리스트에 추가하면 된다.
//    여기서 쓰인 desc는 내림차순을 의미하고, asc는 오름차순을 의미한다.

    public Page<Question> getList(int page, String kw) {
//    public Page<Question> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
//        Pageable pageable = PageRequest.of(page, 10);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
//        return this.questionRepository.findAll(pageable);

        // 검색어를 의미하는 매개변수 kw를 getList 메서드에 추가하고 kw값으로 Specification 객체를 생성하여 findAll 메서드 호출 시 전달
//        // Specification 사용
//        Specification<Question> spec = search(kw);
//        return this.questionRepository.findAll(spec, pageable);

        // @Query 애너테이션 사용
        return this.questionRepository.findAllByKeyword(kw, pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    // 질문 저장
    public void create(String subject, String content, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);

        this.questionRepository.save(q);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }


/*
select
    distinct q.id,
    q.author_id,
    q.content,
    q.create_date,
    q.modify_date,
    q.subject
from question q
left outer join site_user u1 on q.author_id=u1.id
left outer join answer a on q.id=a.question_id
left outer join site_user u2 on a.author_id=u2.id
where
    q.subject like '%스프링%'
    or q.content like '%스프링%'
    or u1.username like '%스프링%'
    or a.content like '%스프링%'
    or u2.username like '%스프링%'

이 쿼리문은 question, answer, site_user 테이블을 대상으로 ‘스프링’이라는 문자열이 포함된 데이터를 검색한다.
그리고 question 테이블을 기준으로 answer, site_user 테이블을 아우터 조인(outer join)하여 문자열 ‘스프링’을 검색한다.
만약 아우터 조인 대신 이너 조인(inner join)을 사용하면 합집합이 아닌 교집합으로 검색되어 데이터 검색 결과가 누락될 수 있다.
그리고 총 3개의 테이블을 대상으로 아우터 조인하여 검색하면 중복된 결과가 나올 수 있어서 select 문에 distinct를 함께 적어 중복을 제거했다.
우리는 이 쿼리문 그대로 사용하지 않고 이전과 마찬가지로 JPA를 사용하여 자바 코드로 만들 것이다.

search 메서드는 검색어를 가리키는 kw를 입력받아 쿼리의 조인문과 where문을 Specification 객체로 생성하여 리턴하는 메서드이다.
코드를 자세히 보면 앞서 살펴본 쿼리를 자바 코드로 그대로 재현한 것임을 알 수 있다.
위 코드에서 사용한 변수들에 대해서 자세히 살펴보자.

q: Root 자료형으로, 즉 기준이 되는 Question 엔티티의 객체를 의미하며 질문 제목과 내용을 검색하기 위해 필요하다.
u1: Question 엔티티와 SiteUser 엔티티를 아우터 조인(여기서는 JoinType.LEFT로 아우터 조인을 적용한다.)하여 만든 SiteUser 엔티티의 객체이다.
Question 엔티티와 SiteUser 엔티티는 author 속성으로 연결되어 있어서 q.join("author")와 같이 조인해야 한다. u1 객체는 질문 작성자를 검색하기 위해 필요하다.
a: Question 엔티티와 Answer 엔티티를 아우터 조인하여 만든 Answer 엔티티의 객체이다.
Question 엔티티와 Answer 엔티티는 answerList 속성으로 연결되어 있어서 q.join("answerList")와 같이 조인해야 한다. a 객체는 답변 내용을 검색할 때 필요하다.
u2: 바로 앞에 작성한 a 객체와 다시 한번 SiteUser 엔티티와 아우터 조인하여 만든 SiteUser 엔티티의 객체로 답변 작성자를 검색할 때 필요하다.
그리고 검색어(kw)가 포함되어 있는지를 like 키워드로 검색하기 위해 제목, 내용, 질문 작성자, 답변 내용, 답변 작성자 각각에 cb.like를 사용하고
최종적으로 cb.or로 OR 검색(여러 조건 중 하나라도 만족하는 경우 해당 항목을 반환하는 검색 조건을 말한다.)이 되게 했다.
앞서 살펴본 쿼리문과 비교해 보면 이 JPA 코드가 어떻게 구성되었는지 좀 더 쉽게 이해할 수 있을 것이다.
*/

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거

                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);

                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }










}
