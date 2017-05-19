import com.alibaba.fastjson.JSON;
import com.xtuer.bean.Demo;
import com.xtuer.mapper.DemoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/spring-beans.xml"})

public class TestDemo {
    @Autowired
    private DemoMapper mapper;

    @Test
    public void insertDemo() {
        Demo demo = new Demo();
        demo.setInfo("你要去哪里");
        System.out.println(mapper.insertDemo(demo));
        System.out.println(demo.getId());
    }

    @Test
    public void findDemo() {
        for (int i = 0; i < 10000; ++i) {
            System.out.println("================== " + i);
            try {
                Demo demo = mapper.findDemoById(1);
                System.out.println(JSON.toJSONString(demo));

                Thread.sleep(1000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Test
    public void findDemos() {
        List<Demo> demos = mapper.findDemosByInfo("Avatar");
        System.out.println(demos);
    }

    @Test
    public void testBoolean() {
        System.out.println(mapper.hasDemo());
    }

    @Test
    public void testCount() {
        System.out.println(mapper.demoCount());
    }
}
