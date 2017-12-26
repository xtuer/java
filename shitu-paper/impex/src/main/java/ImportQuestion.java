import bean.Question;
import com.alibaba.fastjson.JSON;
import mapper.QuestionMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

public class ImportQuestion {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);
        String questionJsonDir = config.getProperty("questionJsonDir"); // 题目的 JSON 文件夹
        Collection<File> questionFiles = FileUtils.listFiles(new File(questionJsonDir), new String[] {"json"}, true);

        QuestionMapper mapper = context.getBean("questionMapper", QuestionMapper.class);

        for (File file : questionFiles) {
            String json = FileUtils.readFileToString(file, "UTF-8");
            Question question = JSON.parseObject(json, Question.class);
            mapper.insertQuestion(question);

            System.out.println(question.getId() + ": " + question.getKnowledgePointId());
        }
    }
}
