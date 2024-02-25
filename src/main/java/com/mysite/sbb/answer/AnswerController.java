package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    // 질문 컨트롤러의 detail 메서드와 달리 createAnswer 메서드의 매개변수에는 @RequestParam(value="content") String content가 추가되었다.
    // 이는 앞서 작성한 템플릿(question_detail.html)에서 답변으로 입력한 내용(content)을 얻으려고 추가한 것이다. 템플릿의 답변 내용에 해당하는
    // <textarea>의 name 속성명이 content이므로 여기서도 변수명을 content로 사용한다. /create/{id}에서 {id}는 질문 엔티티의 id이므로 이 id값으로 질문을 조회하고 값이 없을 경우에는 404 오류가 발생할 것이다.
/*
    AnswerController를 AnswerForm을 사용하도록 변경했다. QuestionForm을 사용했던 방법과 마찬가지로 @Valid와 BindingResult를 사용하여 검증을 진행한다.
    검증에 실패할 경우에는 다시 답변을 등록할 수 있는 question_detail 템플릿을 출력하게 했다.
    이때 question_detail 템플릿은 Question 객체가 필요하므로 model 객체에 Question 객체를 저장한 후에 question_detail 템플릿을 출력해야 한다.
*/
    @PostMapping("/create/{id}")
//    public String createAnswer(Model model, @PathVariable("id") Integer id, @RequestParam(value="content") String content) {
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult) {
        Question question = this.questionService.getQuestion(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
//        this.answerService.create(question, content);

        this.answerService.create(question, answerForm.getContent());

        return String.format("redirect:/question/detail/%s", id);
    }
}
