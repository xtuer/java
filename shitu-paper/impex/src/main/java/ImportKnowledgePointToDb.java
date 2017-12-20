import bean.KnowledgePoint;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import mapper.KnowledgePointMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * 导入单题的 JSON 知识点到数据库
 */
public class ImportKnowledgePointToDb {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);
        KnowledgePointMapper mapper = context.getBean("knowledgePointMapper", KnowledgePointMapper.class);

        String jsonKpDir = config.getProperty("jsonKpDir"); // json 知识点保存目录，知识点文件的名字规范: 高中语文-GYWT033C.json
        File dir = new File(jsonKpDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json")); // 只列出 csv 的文件

        for (File file : files) {
            String json = FileUtils.readFileToString(file, "UTF-8");
            List<KnowledgePoint> nodes = JSON.parseObject(json, new TypeReference<List<KnowledgePoint>>() {});

            for (KnowledgePoint node : nodes) {
                System.out.println(JSON.toJSONString(node));
                mapper.insertKnowledgePoint(node);
            }
        }
    }
}
