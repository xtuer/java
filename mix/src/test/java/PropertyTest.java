import com.xtuer.TokenWhiteListService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:bean.xml"})
public class PropertyTest {
    @Autowired
    private TokenWhiteListService tokenWhiteListService;

    @Test
    public void test() {
        tokenWhiteListService.foo();
    }
}
