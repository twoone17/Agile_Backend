package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "hello.core.member",
        basePackageClasses = AutoAppConfig.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = Configuration.class)
        //앞서 만들어두었던 설정 정보도 함께 등록되고, 실행되어 버린다 이를 빼준다
        //실무에선 다 componentScan 사용한다
)

//이제 @Bean이 없다 !
public class AutoAppConfig {

}
