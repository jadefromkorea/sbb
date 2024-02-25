package com.mysite.sbb.user;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /*
    /user/signup URL이 GET으로 요청되면 회원 가입을 위한 템플릿을 렌더링하고, POST로 요청되면 회원 가입을 진행하도록 했다.
     */
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    /*
    회원 가입 시 password1과 password2가 동일한지를 검증하는 조건문을 추가했다.
    만약 2개의 값이 서로 일치하지 않을 경우에는 bindingResult.rejectValue를 사용하여 입력 받은 2개의 비밀번호가 일치하지 않는다는 오류가 발생하게 했다.
    bindingResult.rejectValue의 매개변수는 순서대로 각각 bindingResult.rejectValue(필드명, 오류 코드, 오류 메시지)를 의미한다.
    여기서 오류 코드는 임의로 passwordInCorrect로 정의했다. 하지만 대형 프로젝트에서는 번역과 관리를 위해 오류 코드를 잘 정의하여 사용해야 한다.
     */
    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        /*
        사용자 ID 또는 이메일 주소가 이미 존재할 경우에는 DataIntegrityViolationException이라는 예외가 발생하므로 '이미 등록된 사용자입니다.'라는 오류 메시지가 화면에 표시하도록 했다.
        그리고 그 밖에 다른 예외들은 해당 예외에 관한 구체적인 오류 메시지를 출력하도록 e.getMessage()를 사용했다.
        여기서 bindingResult.reject(오류 코드, 오류 메시지)는 UserCreateForm의 검증에 의한 오류 외에 일반적인 오류를 발생시킬 때 사용한다.
         */
        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");

            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());

            return "signup_form";
        }

        return "redirect:/";
    }

    /*
    @GetMapping("/login")을 통해 /user/login URL로 들어오는 GET 요청을 이 메서드가 처리한다.
    즉, /user/login URL을 매핑했다. 그리고 매핑한 login 메서드는 login_form.html 템플릿을 출력하도록 만든다.
    실제 로그인을 진행하는 @PostMapping 방식의 메서드는 스프링 시큐리티가 대신 처리하므로 우리가 직접 코드를 작성하여 구현할 필요가 없다.
     */
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
}
