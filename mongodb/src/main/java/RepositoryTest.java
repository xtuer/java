import bean.Person;
import com.xtuer.repository.PersonRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RepositoryTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("mongodb.xml");
        PersonRepository repository = context.getBean("personRepository", PersonRepository.class);
        // MongoTemplate template = context.getBean("mongoTemplate", MongoTemplate.class);
        initData(repository);

        // System.out.println(repository.findAll());
        System.out.println(repository.findByFirstName("Kelly"));
        System.out.println(repository.findByLastName("Giles"));
    }

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
}
