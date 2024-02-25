package com.mysite.sbb.question;

import com.mysite.sbb.answer.AnswerForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/question")
@Slf4j
@RequiredArgsConstructor
@Controller
public class QuestionController {

    // @RequiredArgsConstructor 애너테이션의 생성자 방식으로 questionRepository 객체를 주입했다.
    // @RequiredArgsConstructor는 롬복(Lombok)이 제공하는 애너테이션으로, final이 붙은 속성을 포함하는 생성자를 자동으로 만들어 주는 역할을 한다. 따라서 스프링 부트(Spring Boot)가 내부적으로 QuestionController를 생성할 때 롬복으로 만들어진 생성자에 의해 questionRepository 객체가 자동으로 주입된다.
//    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

//    @GetMapping("/question/list")
//    @ResponseBody
//    public String list() {
//        return "<h1>question list</h1>";
//    }

//    http://localhost:8080/question/list?page=0와 같이 GET 방식으로 요청된 URL에서 page값을 가져오기 위해 list 메서드의 매개변수로 @RequestParam(value="page", defaultValue="0") int page가 추가되었다. URL에 매개변수로 page가 전달되지 않은 경우 기본값은 0이 되도록 설정했다.
//    스프링 부트(Spring Boot)의 페이징 기능을 구현할 때 첫 페이지 번호는 1이 아닌 0이므로 기본값으로 0을 설정해야 한다.
//    GET 방식에서는 값을 전달하기 위해서 ?와 & 기호를 사용한다. 첫 번째 파라미터는 ? 기호를 사용하고 그 이후 추가되는 값은 & 기호를 사용한다.

//    템플릿에 Page 클래스의 객체인 paging을 model에 설정하여 전달했다. paging 객체에는 다음과 같은 속성들이 있는데, 이 속성들은 템플릿에서 페이징을 처리할 때 필요하므로 미리 알아 두자.
//        속성	                            설명
//    paging.isEmpty	        페이지 존재 여부를 의미한다(게시물이 있으면 false, 없으면 true).
//    paging.totalElements	    전체 게시물 개수를 의미한다.
//    paging.totalPages	        전체 페이지 개수를 의미한다.
//    paging.size	            페이지당 보여 줄 게시물 개수를 의미한다.
//    paging.number	            현재 페이지 번호를 의미한다.
//    paging.hasPrevious	    이전 페이지의 존재 여부를 의미한다.
//    paging.hasNext	        다음 페이지의 존재 여부를 의미한다.
    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Question> paging = this.questionService.getList(page);

        model.addAttribute("paging", paging);

        return "question_list";
    }

    @GetMapping("/list_old")
//    @ResponseBody
    public String list_old(Model model) {
//        List<Question> questionList = this.questionRepository.findAll();
        List<Question> questionList = this.questionService.getList_old();

//        System.out.println(">>>>> getSubject: " + questionList.get(1).getSubject());

        // QuestionRepository의 findAll 메서드를 사용하여 질문 목록 데이터인 questionList를 생성하고 Model 객체에 ‘questionList’라는 이름으로 저장했다. 여기서 Model 객체는 자바 클래스(Java class)와 템플릿(template) 간의 연결 고리 역할을 한다. Model 객체에 값을 담아 두면 템플릿에서 그 값을 사용할 수 있다. Model 객체는 따로 생성할 필요 없이 컨트롤러의 메서드에 매개변수로 지정하기만 하면 스프링 부트가 자동으로 Model 객체를 생성한다.
        model.addAttribute("questionList", questionList);

        return "question_list";
    }

    // 요청한 URL인 http://localhost:8080/question/detail/2의 숫자 2처럼 변하는 id값을 얻을 때에는 @PathVariable 애너테이션을 사용한다. 이때 @GetMapping(value = "/question/detail/{id}")에서 사용한 id와 @PathVariable("id")의 매개변수 이름이 이와 같이 동일해야 한다.
    @GetMapping(value = "/detail/{id}")
//    public String detail(Model model, @PathVariable("id") Integer id) {
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }

    // 질물 등록
    @GetMapping("/create")
//    public String questionCreate() {
    public String questionCreate(QuestionForm questionForm) {
//        log.info(">>>>> getSubject: " + questionForm.getSubject());
//        log.info(">>>>> getContent: " + questionForm.getContent());

        return "question_form";
    }

    // 질문 저장
    // 질문을 등록할 때 비어 있는 값으로도 등록할 수 있다는 점을 간과했다.
    // 아무것도 입력하지 않은 상태에서 질문이 등록될 수 없도록 하려면 여러 방법이 있지만 여기서는 폼 클래스를 사용하여 입력값을 체크하는 방법을 사용해 보자.
    // 폼 클래스를 사용해 사용자로부터 입력받은 값을 검증하려면 먼저 Spring Boot Validation 라이브러리가 필요
/*
    questionCreate 메서드의 매개변수를 subject, content 대신 QuestionForm 객체로 변경했다. subject, content 항목을 지닌 폼이 전송되면 QuestionForm의 subject, content 속성이 자동으로 바인딩된다.
    이렇게 이름이 동일하면 함께 연결되어 묶이는 것이 바로 폼의 바인딩 기능이다.

    여기서 QuestionForm 매개변수 앞에 @Valid 애너테이션을 적용했다. @Valid 애너테이션을 적용하면 QuestionForm의 @NotEmpty, @Size 등으로 설정한 검증 기능이 동작한다.
    그리고 이어지는 BindingResult 매개변수는 @Valid 애너테이션으로 검증이 수행된 결과를 의미하는 객체이다.

    BindingResult 매개변수는 항상 @Valid 매개변수 바로 뒤에 위치해야 한다. 만약 두 매개변수의 위치가 정확하지 않다면 @Valid만 적용되어 입력값 검증 실패 시 400 오류가 발생한다.

    따라서 questionCreate 메서드는 bindResult.hasErrors()를 호출하여 오류가 있는 경우에는 다시 제목과 내용을 작성하는 화면으로 돌아가도록 했고, 오류가 없을 경우에만 질문이 등록되도록 만들었다.
*/
    @PostMapping("/create")
//    public String questionCreate(@RequestParam(value="subject") String subject, @RequestParam(value="content") String content) {
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
//        this.questionService.create(subject, content);
        this.questionService.create(questionForm.getSubject(), questionForm.getContent());

        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }
}
