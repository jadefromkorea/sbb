package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<Question> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
//        Pageable pageable = PageRequest.of(page, 10);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        return this.questionRepository.findAll(pageable);
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
}
