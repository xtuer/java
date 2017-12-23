import bean.QuestionKnowledgePoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.Utils;

import java.util.Map;
import java.util.Properties;

public class Test {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);
        String kpJsonDir = config.getProperty("kpJsonDir");

        Map<String, QuestionKnowledgePoint> kps = Utils.prepareQuestionKnowledgePoint(kpJsonDir);
        kps.forEach((key, value) -> {
            System.out.println(key);
        });
    }
}
