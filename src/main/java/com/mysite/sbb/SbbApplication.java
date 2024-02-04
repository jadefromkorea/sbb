package com.mysite.sbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 모든 프로그램에는 프로그램의 시작을 담당하는 파일이 있다.
// 스프링 부트로 만든 프로그램(스프링 부트 애플리케이션)에도 시작을 담당하는 파일이 있는데
// 그 파일이 바로 프로젝트명 + Application.java 파일이다. 스프링 부트 프로젝트를 생성할 때
// 프로젝트명으로 'sbb'라는 이름을 입력하면 다음과 같이 SbbApplication.java 파일이 자동으로 생성된다.
// SbbApplication 클래스에는 반드시 @SpringBootApplication 애너테이션이 적용되어 있어야 한다.
// @SpringBootApplication 애너테이션을 통해 스프링 부트 애플리케이션을 시작할 수 있다.
@SpringBootApplication
public class SbbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbbApplication.class, args);
    }

}
