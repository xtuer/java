package repository;

import bean.Person;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * PersonRepository 对应集合 person，会自动创建
 */
public interface PersonRepository extends MongoRepository<Person, Long> {
    // Spring Data MongoDB 会自动生成下面 2 个函数
    List<Person> findByFirstName(String firstName);
    List<Person> findByLastName(String lastName);
}
