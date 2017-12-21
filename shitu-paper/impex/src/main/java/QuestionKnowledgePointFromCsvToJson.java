import bean.QuestionKnowledgePoint;
import bean.QuestionKnowledgePointTree;
import com.alibaba.fastjson.JSON;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.Utils;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * 处理数据库导出的 csv 的知识点，建立父子关系构建一颗知识点的树，保存到为 json 格式文件:
 * 1. 每个学科的知识点信息保存到一个 csv 文件中，遍历知识点 csv 文件
 * 2. 从知识点 csv 文件名上分离出学科名字和学科编码
 * 3. 从知识点 csv 文件中读取知识点创建 QuestionKnowledgePoint 对象，添加到知识点树的节点中
 * 4. 遍历知识点树的节点构建树，建立父子知识点的关系: tree.build()
 * 5. 遍历知识点树的节点，为每个知识点生成 ID 和设置父知识点的 ID: tree.walk()
 * 6. 把知识点树保存为 json 格式的文件，便于后期使用，例如导入到数据库
 */
public class QuestionKnowledgePointFromCsvToJson {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);

        String kpCsvDir  = config.getProperty("kpCsvDir");  // CSV  知识点文件目录，知识点文件的名字规范: 高中语文-GYWT033C.csv
        String kpJsonDir = config.getProperty("kpJsonDir"); // JSON 知识点保存目录，知识点文件的名字规范: 高中语文-GYWT033C.json
        Collection<File> files = FileUtils.listFiles(new File(kpCsvDir), new String[]{"csv"}, false);

        // 遍历处理所有的知识点文件
        for (File file : files) {
            // 读取每个文件对应的知识点
            String path = file.getAbsolutePath();
            String subjectName = Utils.getKpSubjectName(file.getName());
            String subjectCode = Utils.getKpSubjectCode(file.getName());
            List<QuestionKnowledgePoint> nodes = readKnowledgePoints(path, subjectName, subjectCode, "GB2312");

            // 构建知识点树，然后调用 walk() 生成 ID 和 Parent ID
            QuestionKnowledgePointTree tree = new QuestionKnowledgePointTree();
            tree.getNodes().addAll(nodes);
            tree.build();
            tree.walk();

            // 保存 json 格式的知识点文件
            String fileName = subjectName + "-" + subjectCode + ".json";
            FileUtils.writeStringToFile(new File(kpJsonDir, fileName), JSON.toJSONString(tree.getNodes(), true), "UTF-8");
        }
    }

    /**
     * 从 SQL Server 导出的知识点 csv 文件中读取所有的知识点
     * @param path 知识点的 csv 文件
     * @param subjectName 知识点的科目
     * @param subjectCode 知识点的科目编码
     * @param encoding    知识点的文件编码
     * @return 返回知识点的 list
     * @throws IOException
     */
    public static List<QuestionKnowledgePoint> readKnowledgePoints(String path, String subjectName, String subjectCode, String encoding) throws IOException {
        InputStream in = new FileInputStream(path);
        Reader reader = new InputStreamReader(in, encoding);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);

        List<QuestionKnowledgePoint> nodes = new LinkedList<>();
        nodes.add(new QuestionKnowledgePoint("000", subjectName + "-" + subjectCode, subjectName, subjectCode)); // 根结点

        for (CSVRecord record : records) {
            try {
                String code = record.get("KPID");
                String name = record.get("KPName");

                if ("----".equals(code)) { continue; }
                nodes.add(new QuestionKnowledgePoint(code, name, subjectName, subjectCode));
            } catch (Exception ex) {
                System.out.println(record.toString());
            }
        }

        return nodes;
    }
}
