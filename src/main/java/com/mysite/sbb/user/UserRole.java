package com.mysite.sbb.user;

import lombok.Getter;

/*
스프링 시큐리티는 인증뿐만 아니라 권한도 관리한다. 스프링 시큐리티는 사용자 인증 후에 사용자에게 부여할 권한과 관련된 내용이 필요하다.
그러므로 우리는 사용자가 로그인한 후, ADMIN 또는 USER와 같은 권한을 부여해야 한다.

UserRole은 enum 자료형(열거 자료형)으로 작성했다. 관리자를 의미하는 ADMIN과 사용자를 의미하는 USER라는 상수를 만들었다.
그리고 ADMIN은 ‘ROLE_ADMIN’, USER는 ‘ROLE_USER’라는 값을 부여했다. 그리고 UserRole의 ADMIN과 USER 상수는 값을 변경할 필요가 없으므로 @Setter 없이 @Getter만 사용할 수 있도록 했다.

이 책에서 구현할 SBB는 권한으로 특정 기능을 제어하지 않지만 ADMIN 권한(관리자 권한)을 지닌 사용자가 다른 사람이 작성한 질문이나 답변을 수정 가능하도록 만들 수 있을 것이다.
 */
@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    UserRole(String value) {
        this.value = value;
    }

    private String value;
}
