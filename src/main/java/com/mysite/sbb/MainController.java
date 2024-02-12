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

    // 이와 같이 root 메서드를 추가하고 / URL을 매핑했다. 리턴 문자열 redirect:/question/list는 /question/list URL로 페이지를 리다이렉트하라는 명령어이다. 여기서 리다이렉트란 클라이언트가 요청하면 새로운 URL로 전송하는 것을 의미한다.
    // 이제 http://localhost:8080 페이지에 접속하면 root 메서드가 실행되어 질문 목록이 표시되는 것을 확인할 수 있다.
    // localhost:8080로 접속하면 localhost:8080/question/list로 주소가 바뀌면서 질문 목록이 있는 웹 페이지로 연결된다.
    @GetMapping("/")
    public String root() {
        return "redirect:/question/list";
    }
}
