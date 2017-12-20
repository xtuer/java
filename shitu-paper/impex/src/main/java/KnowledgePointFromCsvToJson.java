import bean.KnowledgePoint;
import bean.KnowledgePointTree;
import com.alibaba.fastjson.JSON;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.Utils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * 处理数据库导出的 csv 的知识点，建立父子关系构建一颗知识点的树，保存到为 json 格式文件
 */
public class KnowledgePointFromCsvToJson {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);

        String csvKpDir  = config.getProperty("csvKpDir");  // csv  知识点文的目录，知识点文件的名字规范: 高中语文-GYWT033C.csv
        String jsonKpDir = config.getProperty("jsonKpDir"); // json 知识点保存目录，知识点文件的名字规范: 高中语文-GYWT033C.json
        File dir = new File(csvKpDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv")); // 只列出 csv 的文件

        // 遍历处理所有的知识点文件
        for (File file : files) {
            // 读取每个文件对应的知识点
            String path = file.getAbsolutePath();
            String subjectName = Utils.getKpSubjectName(file.getName());
            String subjectCode = Utils.getKpSubjectCode(file.getName());
            List<KnowledgePoint> nodes = readKnowledgePoints(path, subjectName, subjectCode, "GB2312");

            // 构建知识点树，然后调用 walk() 生成 ID 和 Parent ID
            KnowledgePointTree tree = new KnowledgePointTree();
            tree.getNodes().addAll(nodes);
            tree.build();
            tree.walk();

            // 保存 json 格式的知识点文件
            String fileName = subjectName + "-" + subjectCode + ".json";
            FileUtils.writeStringToFile(new File(jsonKpDir, fileName), JSON.toJSONString(tree.getNodes(), true), "UTF-8");
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
    public static List<KnowledgePoint> readKnowledgePoints(String path, String subjectName, String subjectCode, String encoding) throws IOException {
        InputStream in = new FileInputStream(path);
        Reader reader = new InputStreamReader(in, encoding);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);

        List<KnowledgePoint> nodes = new LinkedList<>();
        nodes.add(new KnowledgePoint("000", subjectName + "-" + subjectCode, subjectName, subjectCode)); // 根结点

        for (CSVRecord record : records) {
            try {
                String code = record.get("KPID");
                String name = record.get("KPName");

                if ("----".equals(code)) { continue; }
                nodes.add(new KnowledgePoint(code, name, subjectName, subjectCode));
            } catch (Exception ex) {
                System.out.println(record.toString());
            }
        }

        return nodes;
    }
}
