import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:jms.xml");
        context.start(); // 启动 Spring 容器，MessageConsumer 会自动接收到消息
    }
}
