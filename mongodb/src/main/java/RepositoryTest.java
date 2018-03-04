import bean.Person;
import com.xtuer.repository.PersonRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RepositoryTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("mongodb.xml");
        PersonRepository repository = context.getBean("personRepository", PersonRepository.class);
        // MongoTemplate template = context.getBean("mongoTemplate", MongoTemplate.class);
        // initData(repository);
        testReconnect(repository);

        // System.out.println(repository.findAll());
        System.out.println(repository.findByFirstName("Kelly"));
        System.out.println(repository.findByLastName("Giles"));
    }

    // 初始化创建数据
    public static void initData(PersonRepository repository) {
        repository.insert(new Person(1L, "Hodgson",  "Giles"));
        repository.save(new Person(2L, "Richardson", "Giles"));
        repository.insert(new Person(3L, "Kelly",    "Hall"));
        repository.insert(new Person(4L, "Veblen",   "Hall"));
        repository.insert(new Person(5L, "Cowper",   "Gerard"));
        repository.insert(new Person(6L, "Chaucer",  "Oliver"));
        repository.insert(new Person(7L, "Hood",     "David"));
        repository.insert(new Person(8L, "Walton",   "Lester"));
        repository.insert(new Person(9L, "Whitehead", "Eddy"));
    }

    // 定时访问，测试自动重连
    public static void testReconnect(PersonRepository repository) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                System.out.println(repository.findByFirstName("Kelly"));
            } catch (Exception ex) {
                System.out.println("Disconnected"); // 连接断开后这里不会输出，但是如果不放在 try 里就不能继续执行了，诡异
            }
        }, 0, 1L, TimeUnit.SECONDS);
    }
}
