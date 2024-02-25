package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
/*
@Configuration은 이 파일이 스프링의 환경 설정 파일임을 의미하는 애너테이션이다. 여기서는 스프링 시큐리티를 설정하기 위해 사용했다.
@EnableWebSecurity는 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 애너테이션이다.
이 애너테이션을 사용하면 스프링 시큐리티를 활성화하는 역할을 한다. 내부적으로 SecurityFilterChain 클래스가 동작하여 모든 요청 URL에 이 클래스가 필터로 적용되어 URL별로 특별한 설정을 할 수 있게 된다.
스프링 시큐리티의 세부 설정은 @Bean 애너테이션을 통해 SecurityFilterChain 빈을 생성하여 설정할 수 있다.
*/
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 인증되지 않은 모든 페이지의 요청을 허락한다
        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                // 여기서 추가한 .formLogin 메서드는 스프링 시큐리티의 로그인 설정을 담당하는 부분으로,
                // 설정 내용은 로그인 페이지의 URL은 /user/login이고 로그인 성공 시에 이동할 페이지는 루트 URL(/)임을 의미한다.
                /*
                스프링 시큐리티를 통해 로그인을 수행하는 방법에는 여러 가지가 있는데,
                그중에서 가장 간단한 방법으로 SecurityConfig.java와 같은 시큐리티 설정 파일에 사용자 ID와 비밀번호를 직접 등록하여 인증을 처리하는 메모리 방식이 있다.
                하지만 우리는 이미 3-06절에서 회원 가입을 통해 회원 정보를 DB에 저장했으므로 DB에서 회원 정보를 조회하여 로그인하는 방법을 사용할 것이다.
                이제 DB에서 사용자를 조회하는 서비스(UserSecurityService.java)를 만들고, 그 서비스를 스프링 시큐리티에 등록하는 방법을 알아보자.
                하지만 UserSecurityService 서비스를 만들기 전에 UserRepository를 수정하고 UserRole 클래스를 생성하는 등 준비를 해야 한다. 서비스를 활용하기 위한 밑 작업을 진행해 보자.
                 */

                .formLogin((formLogin) -> formLogin
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/"))
        ;
        return http.build();
    }

    /*
    BCryptPasswordEncoder 객체를 직접 new로 생성하는 방식보다는 PasswordEncoder 객체를 빈으로 등록해서 사용하는 것이 좋다.
    왜냐하면 암호화 방식을 변경하면 BCryptPasswordEncoder를 사용한 모든 프로그램을 일일이 찾아다니며 수정해야 하기 때문이다.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

/*
#. H2 콘솔 오류 수정하기
그런데 스프링 시큐리티를 적용하면 H2 콘솔 로그인 시 다음과 같은 403 Forbidden 오류가 발생한다. 403 Forbidden은 작동 중인 서버에 클라이언트의 요청이 들어왔으나,
서버가 클라이언트의 접근을 거부했을 때 반환하는 HTTP 오류 코드이다. 이 오류는 서버 또는 서버에 있는 파일 등에 접근 권한이 없을 경우에 발생한다.

403 Forbidden 오류가 발생하는 이유를 좀 더 구체적으로 설명하면, 스프링 시큐리티의 CSRF 방어 기능에 의해 H2 콘솔 접근이 거부되기 때문이다.
CSRF는 웹 보안 공격 중 하나로, 조작된 정보로 웹 사이트가 실행되도록 속이는 공격 기술이다.
스프링 시큐리티는 이러한 공격을 방지하기 위해 CSRF 토큰을 세션을 통해 발행하고,
웹 페이지에서는 폼 전송 시에 해당 토큰을 함께 전송하여 실제 웹 페이지에서 작성한 데이터가 전달되는지를 검증한다.

토큰이란 요청을 식별하고 검증하는 데 사용하는 특수한 문자열 또는 값을 의미한다.
세션이란 사용자의 상태를 유지하고 관리하는 데 사용하는 기능이다.
1) 이 오류를 해결하기 전에 다음과 같이 [질문 등록(http://localhost:8080/question/create)] 화면을 열고 브라우저의 ‘페이지 소스 보기’ 기능을 이용하여 질문 등록 화면의 소스를 잠시 확인해 보자.
2) 그러면 다음과 같이 질문 등록 화면의 소스를 볼 수 있다.
다음과 같은 input 요소가 <form> 태그 안에 자동으로 생성된 것을 확인할 수 있다.
<input type="hidden" name="_csrf" value="ELCsIKgKv7yGzeFZsXxrCtAcVBiiS-lvBrP8b-1scsRpPlrWHJoKfFsw4ioyr-thtgFFfbLF6eSCUctFCYICYW2l4gC57o4W1"/>
스프링 시큐리티에 의해 이와 같은 CSRF 토큰이 자동으로 생성된다.

CSRF 토큰은 서버에서 생성되는 임의의 값으로 페이지 요청 시 항상 다른 값으로 생성된다. 때문에 여러분의 화면과 다소 차이가 있다.
스프링 시큐리티는 이런 식으로 페이지에 CSRF 토큰을 발행하여 이 값이 다시 서버로 정확하게 들어오는지를 확인하는 과정을 거친다.
만약 CSRF 토큰이 없거나 해커가 임의의 CSRF 토큰을 강제로 만들어 전송한다면 스프링 시큐리티에 의해 차단될 것이다. 정리하자면,
H2 콘솔은 스프링 프레임워크가 아니므로 CSRF 토큰을 발행하는 기능이 없어 이와 같은 403 오류가 발생한 것이다.
H2 콘솔은 스프링과 상관없는 일반 애플리케이션이다.

3) 스프링 시큐리티가 CSRF 처리 시 H2 콘솔은 예외로 처리할 수 있도록 다음과 같이 설정 파일을 수정하자.
http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
            .csrf((csrf) -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));
이와 같이 /h2-console/로 시작하는 모든 URL은 CSRF 검증을 하지 않는다는 설정을 추가했다. 이렇게 수정하고 로컬 서버를 재시작한 후 다시 H2 콘솔에 접속해 보자.

4) 이제 CSRF 검증에서 예외 처리되어 로그인은 잘 수행된다. 하지만 다음과 같이 화면이 깨져 보인다.
이와 같은 오류가 발생하는 원인은 H2 콘솔의 화면이 프레임(frame) 구조로 작성되었기 때문이다.
즉, H2 콘솔 UI(user interface) 레이아웃이 이 화면처럼 작업 영역이 나눠져 있음을 의미한다.
스프링 시큐리티는 웹 사이트의 콘텐츠가 다른 사이트에 포함되지 않도록 하기 위해 X-Frame-Options 헤더의 기본값을 DENY로 사용하는데,
프레임 구조의 웹 사이트는 이 헤더의 값이 DENY인 경우 이와 같이 오류가 발생한다.

스프링 부트에서 X-Frame-Options 헤더는 클릭재킹 공격을 막기 위해 사용한다.
클릭재킹은 사용자의 의도와 다른 작업이 수행되도록 속이는 보안 공격 기술이다.

5) 이 문제를 해결하기 위해 다음과 같이 설정 파일을 수정하자.
http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
            .csrf((csrf) -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
            .headers((headers) -> headers.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
        ;
이와 같이 URL 요청 시 X-Frame-Options 헤더를 DENY 대신 SAMEORIGIN으로 설정하여 오류가 발생하지 않도록 했다. X-Frame-Options 헤더의 값으로 SAMEORIGIN을 설정하면 프레임에 포함된 웹 페이지가 동일한 사이트에서 제공할 때에만 사용이 허락된다.

스프링 시큐리티를 사용하면 웹 프로그램(애플리케이션)의 보안을 강화하고 사용자 인증 및 권한 부여를 효과적으로 관리할 수 있으며, 외부 공격으로부터 시스템을 보호하는 데 도움을 얻을 수 있다.
*/







