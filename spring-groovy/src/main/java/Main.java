import com.xtuer.service.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-beans.xml");
        HelloService service = context.getBean("helloService", HelloService.class);
        System.out.println(service.hello());
    }
}
