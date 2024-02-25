package com.mysite.sbb.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
    /*
    subject 속성에는 @NotEmpty와 @Size 애너테이션이 적용되었다. @NotEmpty는 해당 값이 Null 또는 빈 문자열("")을 허용하지 않음을 의미한다.
    그리고 여기에 사용한 message는 검증이 실패할 경우 화면에 표시할 오류 메시지이다. @Size(max=200)은 최대 길이가 200 바이트(byte)를 넘으면 안 된다는 의미로,
    이와 같이 설정하면 길이가 200 바이트보다 큰 제목이 입력되면 오류가 발생한다. content 속성 역시 @NotEmpty 애너테이션을 적용하여 빈 값을 허용하지 않도록 했다.

    폼 클래스는 입력값 검증할 때뿐만 아니라 입력 항목을 바인딩할 때도 사용한다. 즉, question_form.html 템플릿의 입력 항목인 subject와 content가 폼 클래스의 subject, content 속성과 바인딩된다.
    여기서 바인딩이란 템플릿의 항목과 form 클래스의 속성이 매핑되는 과정을 말한다.
    */
    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=200)
    private String subject;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;
}
