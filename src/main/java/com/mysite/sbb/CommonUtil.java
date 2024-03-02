package com.mysite.sbb;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {
    /*
    @Component 애너테이션을 사용하여 CommonUtil 클래스를 생성했다.
    이렇게 하면 이제 CommonUtil 클래스는 스프링 부트가 관리하는 빈으로 등록된다.
    이렇게 빈으로 등록된 컴포넌트는 템플릿에서 사용할 수 있다.
    CommonUtil 클래스에는 markdown 메서드를 생성했다.
    markdown 메서드는 마크다운 텍스트를 HTML 문서로 변환하여 리턴한다.
    즉, 마크다운 문법이 적용된 일반 텍스트를 변환된 HTML로 리턴한다.
     */
    public String markdown(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        return renderer.render(document);
    }
}
