package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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


//        this.answerService.create(question, answerForm.getContent(), siteUser);
//        return String.format("redirect:/question/detail/%s", id);
        Answer answer = this.answerService.create(question, answerForm.getContent(), siteUser);
        // 리다이렉트되는 질문 상세 페이지 URL에 #answer_%s를 이와 같이 삽입하여 앵커를 추가
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }

        Answer answer = this.answerService.getAnswer(id);

        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        this.answerService.modify(answer, answerForm.getContent());

//        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
        // 리다이렉트되는 질문 상세 페이지 URL에 #answer_%s를 이와 같이 삽입하여 앵커를 추가
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        Answer answer = this.answerService.getAnswer(id);

        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.answerService.delete(answer);

        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        /*
        추천은 로그인한 사람만 가능해야 하므로 @PreAuthorize("isAuthenticated( )") 애너테이션을 적용했다.
        그리고 앞서 작성한 AnswerService의 vote 메서드를 호출하여 추천인을 저장한다.
        오류가 없다면 추천인을 저장한 후 질문 상세 화면으로 리다이렉트한다.
         */
        this.answerService.vote(answer, siteUser);

//        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }
}
