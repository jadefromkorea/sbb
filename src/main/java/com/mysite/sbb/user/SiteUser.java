package com.mysite.sbb.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {
/*
username, email 속성에는 @Column(unique = true)로 지정했다. 여기서 unique = true는 유일한 값만 저장할 수 있음을 의미한다.
즉, 값을 중복되게 저장할 수 없음을 말한다. 이렇게 해야 username과 email에 동일한 값이 저장되는 것을 막을 수 있다.

SITE_USER 테이블과 데이터 열들 그리고 unique로 설정한 속성들로 인해 생긴 UK_로 시작하는 인덱스들을 확인할 수 있다.
unique=true로 지정한 속성들은 DB에 유니크 인덱스로 생성된다.
여기서 쓰인 UK는 unique key의 줄임말이다.
 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;
}

/*
PK (Primary Key)
기본키라고 하며 엔티티를 식별하는 대표 키, NULL일 수 없다.

FK (Foreign Key)
다른 테이블의 PK를 참조하는 Key 동일한 Domain을 갖는다.
어떠한 테이블에 존재하는 다른 테이블의 정보 이기 때문에 외래 키이다.
참조하고자 하는 Column은 PK 또는 UK이여야 한다.

UK (Unique Key)
테이블 내에서 해당 Column의 값은 항상 유일하다.
PK와 다른 점은 NULL값이 중복 가능하다.

CK (Check)
특정 Column에 값을 입력할 수 있는 범위나 조건을 지정한다. (제약 조건)
 */
