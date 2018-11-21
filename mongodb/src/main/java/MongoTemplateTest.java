import bean.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class MongoTemplateTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("mongodb-security.xml");
        MongoTemplate mongoTemplate = context.getBean("mongoTemplate", MongoTemplate.class);
        Criteria criteria = Criteria.where("firstName").is("Biao");
        List<Person> persons = mongoTemplate.find(Query.query(criteria), Person.class, "person");
        System.out.println(persons);
    }
}
