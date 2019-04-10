import com.alibaba.fastjson.JSON;
import com.xtuer.bean.Demo;
import com.xtuer.mapper.DemoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/spring-beans.xml"})
public class DemoTest {
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
        Demo demo = mapper.findDemoById(6);
        System.out.println(JSON.toJSONString(demo));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(demo.getCreatedAt()));
    }

    @Test
    public void findDemos() {
        List<Demo> demos;

        // 查找到，返回 Demo 的 List 对象
        demos = mapper.findDemosByInfo("Alice");
        System.out.println(demos);

        // 查找不到则返回空的 List，而不是 null
        demos = mapper.findDemosByInfo("Avatar");
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
