package com.mysite.sbb.question;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
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
    private final UserService userService;

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
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
//    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
//        Page<Question> paging = this.questionService.getList(page);
        /*
        검색어에 해당하는 kw 매개변수를 추가했고 기본값으로 빈 문자열을 설정했다.
        검색어가 입력되지 않을 경우 kw값이 null이 되는 것을 방지하기 위해 빈 문자열을 기본값으로 설정한다.
        그리고 화면에서 입력한 검색어를 화면에 그대로 유지하기 위해 model.addAttribute("kw", kw)로 kw값을 저장했다.
        이제 화면에서 검색어가 입력되면 kw값이 매개변수로 들어오고 해당 값으로 질문 목록이 검색되어 조회될 것이다.
         */
        Page<Question> paging = this.questionService.getList(page, kw);

        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);

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

    /*
        이는 principal 객체가 널(null)이라서 발생한 오류이다. principal 객체는 로그인을 해야만 생성되는 객체인데 현재는 로그아웃 상태이므로 principal 객체에 값이 없어 오류가 발생하는 것이다.
        2) 이 문제를 해결하려면 principal 객체를 사용하는 메서드에 @PreAuthorize("isAuthenticated()") 애너테이션을 사용해야 한다.
        @PreAuthorize("isAuthenticated()") 애너테이션이 붙은 메서드는 로그인한 경우에만 실행된다.
        즉, 이 애너테이션을 메서드에 붙이면 해당 메서드는 로그인한 사용자만 호출할 수 있다.
        @PreAuthorize("isAuthenticated()") 애너테이션이 적용된 메서드가 로그아웃 상태에서 호출되면 로그인 페이지로 강제 이동된다.
        로그아웃 상태에서 [질문 등록] 버튼을 누르면 로그인 페이지로 이동한다. 로그인을 완료하면 이전에 요청한 질문 등록 페이지가 등장한다.
        이는 로그인 후에 원래 가려고 했던 페이지로 리다이렉트시키는 스프링 시큐리티의 기능 덕분에 가능한 것이다.
         */
    @PreAuthorize("isAuthenticated()")
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
    /*
    이는 principal 객체가 널(null)이라서 발생한 오류이다. principal 객체는 로그인을 해야만 생성되는 객체인데 현재는 로그아웃 상태이므로 principal 객체에 값이 없어 오류가 발생하는 것이다.
    2) 이 문제를 해결하려면 principal 객체를 사용하는 메서드에 @PreAuthorize("isAuthenticated()") 애너테이션을 사용해야 한다.
    @PreAuthorize("isAuthenticated()") 애너테이션이 붙은 메서드는 로그인한 경우에만 실행된다.
    즉, 이 애너테이션을 메서드에 붙이면 해당 메서드는 로그인한 사용자만 호출할 수 있다.
    @PreAuthorize("isAuthenticated()") 애너테이션이 적용된 메서드가 로그아웃 상태에서 호출되면 로그인 페이지로 강제 이동된다.
    먼저, QuestionController부터 다음과 같이 수정해 보자.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
//    public String questionCreate(@RequestParam(value="subject") String subject, @RequestParam(value="content") String content) {
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
//        this.questionService.create(subject, content);
        // principal.getName()을 호출하면 현재 로그인한 사용자의 사용자명(사용자ID)을 알 수 있다.
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);

        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        /*
        이와 같이 questionModify 메서드를 추가했다. 만약 현재 로그인한 사용자와 질문의 작성자가 동일하지 않을 경우에는 '수정 권한이 없습니다.'라는 오류가 발생하도록 했다.
        그리고 수정할 질문의 제목과 내용을 화면에 보여 주기 위해 questionForm 객체에 id값으로 조회한 질문의 제목(subject)과 내용(object)의 값을 담아서 템플릿으로 전달했다.
        이 과정이 없다면 질문 수정 화면에 '제목', '내용'의 값이 채워지지 않아 비워져 보일 것이다.
        그런데 여기서 한 가지 짚고 넘어가야 할 것이 있다. 질문을 수정할 수 있는 새로운 템플릿을 만들지 않고 질문을 등록했을 때 사용한 question_form.html 템플릿을 사용한다는 점이다.
        */
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());

        return "question_form";
    }

    /*
    POST 형식의 /question/modify/{id} 요청을 처리하기 위해 이와 같이 questionModify 메서드를 추가했다.
    questionModify 메서드는 questionForm의 데이터를 검증하고 로그인한 사용자와 수정하려는 질문의 작성자가 동일한지도 검증한다.
    검증이 통과되면 QuestionService에서 작성한 modify 메서드를 호출하여 질문 데이터를 수정한다.
    그리고 수정이 완료되면 질문 상세 화면(/question/detail/(숫자))으로 리다이렉트한다.
    */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        Question question = this.questionService.getQuestion(id);
        /*
        이와 같이 questionModify 메서드를 추가했다. 만약 현재 로그인한 사용자와 질문의 작성자가 동일하지 않을 경우에는 '수정 권한이 없습니다.'라는 오류가 발생하도록 했다.
        그리고 수정할 질문의 제목과 내용을 화면에 보여 주기 위해 questionForm 객체에 id값으로 조회한 질문의 제목(subject)과 내용(object)의 값을 담아서 템플릿으로 전달했다.
        이 과정이 없다면 질문 수정 화면에 '제목', '내용'의 값이 채워지지 않아 비워져 보일 것이다.
        그런데 여기서 한 가지 짚고 넘어가야 할 것이 있다. 질문을 수정할 수 있는 새로운 템플릿을 만들지 않고 질문을 등록했을 때 사용한 question_form.html 템플릿을 사용한다는 점이다.
        */
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());

        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        /*
        사용자가 [삭제] 버튼을 클릭했다면 URL로 전달받은 id값을 사용하여 Question 데이터를 조회한 후,
        로그인한 사용자와 질문 작성자가 동일할 경우 앞서 작성한 서비스를 이용하여 질문을 삭제하게 했다.
        그리고 질문을 삭제한 후에는 질문 목록 화면(/)으로 돌아갈 수 있도록 했다.
        */
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.questionService.delete(question);

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        /*
        다른 기능과 마찬가지로 추천 기능도 로그인한 사람만 사용할 수 있도록 @PreAuthorize("isAuthenticated( )") 애너테이션을 적용했다.
        그리고 앞서 작성한 QuestionService의 vote 메서드를 호출하여 사용자(siteUser)를 추천인(voter)으로 저장했다.
        오류가 없다면 추천인을 저장한 후 질문 상세 화면으로 리다이렉트한다.
         */
        this.questionService.vote(question, siteUser);

        return String.format("redirect:/question/detail/%s", id);
    }

}
