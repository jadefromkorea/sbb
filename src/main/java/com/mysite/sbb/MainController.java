package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/sbb")
    @ResponseBody
    // @ResponseBody 애너테이션은 URL 요청에 대한 응답으로 문자열을 리턴하라는 의미로 쓰였다.
    // 만약 @ResponseBody 애너테이션을 생략한다면 스프링 부트는 'index'라는 문자열을 리턴하는 대신 index라는 이름의 템플릿 파일을 찾게 된다.
    public String index() {
        return "안녕하세요 sbb에 오신 것을 환영합니다";
    }
}
