package bootimport;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 练习 Spring 的 @Import，参考 https://www.toutiao.com/i6964714416520692236/
 */
public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Tom tom = context.getBean(Tom.class);
        System.out.println(tom);
    }
}
