package com.mysite.sbb;


import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

//    @BeforeAll
//    public void BeforeAll() {
//        System.out.println("@BeforeEach");
//    }

    @BeforeEach
    public void BeforeEach() {
        System.out.println("@BeforeEach");
    }

//    @AfterAll
//    public void AfterAll() {
//        System.out.println("@AfterAll");
//    }

    @AfterEach
    public void AfterEach() {
        System.out.println("@AfterEach");
    }

    @Test
    void testJpa01() {
        System.out.println(">>>>> testJpa01");

        Question q1 = new Question();
        q1.setSubject("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해서 알고 싶습니다.");
        q1.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q1);  // 첫번째 질문 저장

        Question q2 = new Question();
        q2.setSubject("스프링부트 모델 질문입니다.");
        q2.setContent("id는 자동으로 생성되나요?");
        q2.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q2);  // 두번째 질문 저장
    }

    @Test
    void testJpa02() {
        System.out.println(">>>>> testJpa02");

        List<Question> all = this.questionRepository.findAll();
        assertEquals(2, all.size());

        Question q = all.get(0);
        assertEquals("sbb가 무엇인가요?", q.getSubject());
    }

    @Test
    void testJpa03() {
        System.out.println(">>>>> testJpa03");

        Optional<Question> oq = this.questionRepository.findById(1);

        if(oq.isPresent()) {
            Question q = oq.get();

            assertEquals("sbb가 무엇인가요?", q.getSubject());
        }
    }

    @Test
    void testJpa04() {
        System.out.println(">>>>> testJpa04");

        Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");

        assertEquals(1, q.getId());
    }

    @Test
    void testJpa05() {
        System.out.println(">>>>> testJpa05");

        Question q = this.questionRepository.findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");

        assertEquals(1, q.getId());
    }

    @Test
    void testJpa06() {
        System.out.println(">>>>> testJpa06");

        List<Question> qList = this.questionRepository.findBySubjectLike("sbb%");
        Question q = qList.get(0);

        assertEquals("sbb가 무엇인가요?", q.getSubject());
    }

    @Test
    void testJpa07() {
        System.out.println(">>>>> testJpa07");

        Optional<Question> oq = this.questionRepository.findById(1);

        assertTrue(oq.isPresent());

        Question q = oq.get();
        q.setSubject("수정된 제목");

        this.questionRepository.save(q);
    }

    @Test
    void testJpa08() {
        System.out.println(">>>>> testJpa08");

        assertEquals(2, this.questionRepository.count());

        Optional<Question> oq = this.questionRepository.findById(1);

        assertTrue(oq.isPresent());

        Question q = oq.get();

        this.questionRepository.delete(q);

        assertEquals(1, this.questionRepository.count());
    }

    @Transactional
    @Test
    void testJpa09() {
        System.out.println(">>>>> testJpa09");

        Optional<Question> oq = this.questionRepository.findById(2);

        assertTrue(oq.isPresent());

        Question q = oq.get();

        Answer a = new Answer();
        a.setContent("네 자동으로 생성됩니다.");
        a.setQuestion(q);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
        a.setCreateDate(LocalDateTime.now());

        this.answerRepository.save(a);
    }

    @Test
    void testJpa10() {
        System.out.println(">>>>> testJpa10");

        Optional<Answer> oa = this.answerRepository.findById(2);

        assertTrue(oa.isPresent());

        Answer a = oa.get();

        assertEquals(2, a.getQuestion().getId());
    }

    @Transactional
    @Test
    void testJpa11() {
        System.out.println(">>>>> testJpa11");

        Optional<Question> oq = this.questionRepository.findById(2);

        assertTrue(oq.isPresent());

        Question q = oq.get();

        List<Answer> answerList = q.getAnswerList();

        assertEquals(1, answerList.size());
        assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
    }

//    @Transactional
//    @Test
//    void testJpa12() {
//        System.out.println(">>>>> testJpa12");
//
//        Question question = new Question();
//        question.setSubject("질문 제목 1");
//        question.setContent("질문 내용 1");
//        question.setCreateDate(LocalDateTime.now());
//
//        Answer answer = new Answer();
//        answer.setContent("답변 내용 1");
//        answer.setQuestion(question);
//        answer.setCreateDate(LocalDateTime.now());
//
////        List<Answer> answerList = new ArrayList<>();
////        answerList.add(answer);
////
////        question.setAnswerList(answerList);
//
////        this.questionRepository.save(question);
//
//        this.answerRepository.save(answer);
//
//        Question q = this.questionRepository.findBySubject("질문 제목 1");
//        assertEquals("질문 제목 1", q.getSubject());
//
//        assertEquals(1, this.answerRepository.count());
//    }

    @Test
    @DisplayName("300개의 테스트 데이터를 생성")
    void makeTestData() {
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";

            this.questionService.create(subject, content, null);
        }
    }




}
