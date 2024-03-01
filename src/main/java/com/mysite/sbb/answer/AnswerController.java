package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    // 질문 컨트롤러의 detail 메서드와 달리 createAnswer 메서드의 매개변수에는 @RequestParam(value="content") String content가 추가되었다.
    // 이는 앞서 작성한 템플릿(question_detail.html)에서 답변으로 입력한 내용(content)을 얻으려고 추가한 것이다. 템플릿의 답변 내용에 해당하는
    // <textarea>의 name 속성명이 content이므로 여기서도 변수명을 content로 사용한다. /create/{id}에서 {id}는 질문 엔티티의 id이므로 이 id값으로 질문을 조회하고 값이 없을 경우에는 404 오류가 발생할 것이다.
/*
    AnswerController를 AnswerForm을 사용하도록 변경했다. QuestionForm을 사용했던 방법과 마찬가지로 @Valid와 BindingResult를 사용하여 검증을 진행한다.
    검증에 실패할 경우에는 다시 답변을 등록할 수 있는 question_detail 템플릿을 출력하게 했다.
    이때 question_detail 템플릿은 Question 객체가 필요하므로 model 객체에 Question 객체를 저장한 후에 question_detail 템플릿을 출력해야 한다.
*/
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
    @PostMapping("/create/{id}")
//    public String createAnswer(Model model, @PathVariable("id") Integer id, @RequestParam(value="content") String content) {
    // 현재 로그인한 사용자의 정보를 알려면 스프링 시큐리티가 제공하는 Principal 객체를 사용해야 한다.
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        // principal.getName()을 호출하면 현재 로그인한 사용자의 사용자명(사용자ID)을 알 수 있다.
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
//        this.answerService.create(question, content);


        this.answerService.create(question, answerForm.getContent(), siteUser);

        return String.format("redirect:/question/detail/%s", id);
    }
}
