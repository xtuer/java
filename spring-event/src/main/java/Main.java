import com.xtuer.bean.User;
import com.xtuer.event.publisher.UserEventPublisher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-beans.xml");
        UserEventPublisher publisher = context.getBean("userEventPublisher", UserEventPublisher.class);

        User user = new User(1, "Alice", "12345678");
        publisher.publish(user, "CREATE_USER");

        user.setPassword("Passw0rd");
        publisher.publish(user, "UPDATE_USER");
    }
}
