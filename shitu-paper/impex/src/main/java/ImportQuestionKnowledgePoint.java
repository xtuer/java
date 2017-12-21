import bean.QuestionKnowledgePoint;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import mapper.QuestionKnowledgePointMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * 导入单题的 JSON 知识点到数据库:
 * 1. 遍历知识点文件所在目录读取 json 格式的知识点文件
 * 2. 读取 json 格式的知识点，转换为知识点的列表
 * 3. 遍历知识点列表，把知识点插入数据库(因为知识点的名字、ID、父节点 ID 等关系都是构建好的，所以直接插入到数据库即可)
 */
public class ImportQuestionKnowledgePoint {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);
        QuestionKnowledgePointMapper mapper = context.getBean("questionKnowledgePointMapper", QuestionKnowledgePointMapper.class);

        String jsonKpDir = config.getProperty("jsonKpDir"); // json 知识点保存目录，知识点文件的名字规范: 高中语文-GYWT033C.json
        Collection<File> files = FileUtils.listFiles(new File(jsonKpDir), new String[]{"json"}, false);

        for (File file : files) {
            String json = FileUtils.readFileToString(file, "UTF-8");
            List<QuestionKnowledgePoint> nodes = JSON.parseObject(json, new TypeReference<List<QuestionKnowledgePoint>>() {});

            for (QuestionKnowledgePoint node : nodes) {
                System.out.println(JSON.toJSONString(node));
                mapper.insertKnowledgePoint(node);
            }
        }
    }
}
