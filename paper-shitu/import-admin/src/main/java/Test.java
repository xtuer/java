import com.xtuer.util.CommonUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

public class Test {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/spring-beans.xml");
        Properties props = context.getBean("config", Properties.class);
        System.out.println(CommonUtils.getStrings(props, "paperInfo"));
    }
}
