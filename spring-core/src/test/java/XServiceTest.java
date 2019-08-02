import com.xtuer.bean.User;
import com.xtuer.service.XService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:bean.xml"})
public class XServiceTest {
    @Autowired
    private XService service;

    @Test
    public void testFoo() {
        service.foo(10010);
    }

    @Test
    public void testBar() {
        service.bar(new User("Bob"));
    }

    @Test
    public void testNoom() {
        service.noom();
    }

    @Test
    public void testGoo() {
        service.goo(10010);
    }
}
