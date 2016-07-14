import com.xtuer.bean.Demo;
import com.xtuer.mapper.DemoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/spring-beans.xml"})

public class TestDemo {
    @Autowired
    private DemoMapper mapper;

    @Test
    public void insertDemo() {
        Demo demo = new Demo();
        demo.setDescription("你要去哪里");
        System.out.println(mapper.insertDemo(demo));
        System.out.println(demo.getId());
    }

    @Test
    public void findDemo() {
        Demo demo = mapper.findDemoById(1);
        System.out.println(demo.getDescription());
    }
}
