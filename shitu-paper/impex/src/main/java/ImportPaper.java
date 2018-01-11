import bean.Paper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import mapper.PaperMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ImportPaper {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);
        String paperJson = config.getProperty("paperJson"); // 试卷目录 Json 文件
        String json = FileUtils.readFileToString(new File(paperJson), "UTF-8");

        List<Paper> papers = JSON.parseObject(json, new TypeReference<List<Paper>>() {});
        PaperMapper paperMapper = context.getBean("paperMapper", PaperMapper.class);

        for (Paper paper : papers) {
            System.out.println(paper.getPaperId());
            paperMapper.insertPaper(paper);
        }
    }
}
