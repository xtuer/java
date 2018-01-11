import bean.PaperDirectory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import mapper.PaperDirectoryMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ImportPaperDirectory {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);
        String paperDirectoryJson = config.getProperty("paperDirectoryJson"); // 试卷目录 Json 文件
        String json = FileUtils.readFileToString(new File(paperDirectoryJson), "UTF-8");

        List<PaperDirectory> directories = JSON.parseObject(json, new TypeReference<List<PaperDirectory>>() {});
        PaperDirectoryMapper directoryMapper = context.getBean("paperDirectoryMapper", PaperDirectoryMapper.class);

        for (PaperDirectory directory : directories) {
            directoryMapper.insertPaperDirectory(directory);
        }
    }
}
