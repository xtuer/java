import com.alibaba.fastjson.JSON;
import com.xtuer.bean.User;
import com.xtuer.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/spring-beans.xml"})
public class UserTest {
    @Autowired
    private UserMapper mapper;

    @Test
    public void findUser() {
        User user = mapper.userByName("biao");
        System.out.println(JSON.toJSONString(user, true));
    }
}
