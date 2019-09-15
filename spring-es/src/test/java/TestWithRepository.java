import com.xtuer.bean.User;
import com.xtuer.repo.UserRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:bean.xml"})
public class TestWithRepository {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void initIndex() {
        userRepository.deleteAll();
        userRepository.save(new User(1, "Marjory Joyce", "中华人民共和国公民"));
        userRepository.save(new User(2, "Lamb Cook", "中华人民"));
        userRepository.save(new User(3, "Katharine Warren", "和"));
        userRepository.save(new User(4, "Dillon George", "共和国公民"));
        userRepository.save(new User(5, "Dillon George", "你和我"));
    }

    @Test
    public void findByNickname() {
        // 不使用分页
        System.out.println(userRepository.findByNickname("共和国公民"));

        // 使用分页
        List<User> users = userRepository.findByNickname("共和国公民", PageRequest.of(0, 11));
        System.out.println(users);
    }

    @Test
    public void searchByNickname() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("nickname", "共和国公民"))
                .withPageable(PageRequest.of(0, 11))
                .build();
        Page<User> users = userRepository.search(query);
        users.forEach(System.out::println);
    }
}
