//package hello.core.scan.filter;
//
//import org.junit.Test;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.FilterType;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.springframework.context.annotation.ComponentScan.*;
//import static org.testng.Assert.assertThrows;
//
//public class ComponentFilterAppConfigTest {
//
//    @Test
//    void filterScan() {
//        ApplicationContext ac = new
//                AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
//        BeanA beanA = ac.getBean("beanA", BeanA.class);
//        Assertion
//        assertThrows(
//                NoSuchBeanDefinitionException.class,
//                () -> ac.getBean("beanB", BeanB.class));
//    }
//
//    @Configuration
//    @ComponentScan(includeFilters = @Filter(type = FilterType.ANNOTATION,classes = MyIncludeComponent.class),
//                    excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class)
//    )
//
//     static class ComponentFilterAppConfig{
//
//    }
//}
