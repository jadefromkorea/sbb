package com.mysite.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
    /*
    username은 입력받는 데이터의 길이가 3~25 사이여야 한다는 검증 조건을 설정했다.
    @Size는 문자열의 길이가 최소 길이(min)와 최대 길이(max) 사이에 해당하는지를 검증한다.
    password1과 password2는 ‘비밀번호’와 ‘비밀번호 확인’에 대한 속성이다.
    로그인할 때는 비밀번호가 한 번만 필요하지만 회원 가입 시에는 입력한 비밀번호가 정확한지 확인하기 위해 2개의 필드가 필요하므로 이와 같이 작성한다.
    그리고 email 속성에는 @Email 애너테이션이 적용되었다. @Email은 해당 속성의 값이 이메일 형식과 일치하는지를 검증한다.
     */
    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;
}
