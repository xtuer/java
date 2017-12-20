import com.alibaba.fastjson.JSON;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * 处理数据库导出的 csv 的知识点，建立父子关系构建一颗知识点的树，保存到文件
 */
public class KnowledgePointHandler {
    public static void main(String[] args) throws IOException {
        String saveDir = "/Users/Biao/Documents/套卷/知识点"; // 保存的目录

        String[][] meta = {
                // path, subject, subject code
                {"/Users/Biao/Documents/套卷/知识点/高中语文-GYWT033C.csv", "高中语文", "GYWT033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中地理-GDLT030C.csv", "高中地理", "GDLT030C"},
                {"/Users/Biao/Documents/套卷/知识点/高中历史-GLST033C.csv", "高中历史", "GLST033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中数学-GZSX060B.csv", "高中数学", "GZSX060B"},
                {"/Users/Biao/Documents/套卷/知识点/高中语文-GZYW033C.csv", "高中语文", "GZYW033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中历史-GZLS033C.csv", "高中历史", "GZLS033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中地理-GDLZ033C.csv", "高中地理", "GDLZ033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中生物-GZSW033C.csv", "高中生物", "GZSW033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中英语-GYYK034C.csv", "高中英语", "GYYK034C"},
                {"/Users/Biao/Documents/套卷/知识点/高中地理-GZDL033C.csv", "高中地理", "GZDL033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中化学-GZHX062A.csv", "高中化学", "GZHX062A"},
                {"/Users/Biao/Documents/套卷/知识点/高中英语-GZYY033C.csv", "高中英语", "GZYY033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中生物-GSWT033C.csv", "高中生物", "GSWT033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中数学-GSZH030C.csv", "高中数学", "GSZH030C"},
                {"/Users/Biao/Documents/套卷/知识点/高中政治-GZZZ033C.csv", "高中政治", "GZZZ033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中生物-ZSWZ033C.csv", "高中生物", "ZSWZ033C"},
                {"/Users/Biao/Documents/套卷/知识点/高中物理-GZWL061A.csv", "高中物理", "GZWL061A"},
                {"/Users/Biao/Documents/套卷/知识点/高中物理-GZWL061B.csv", "高中物理", "GZWL061B"},
        };

        // 遍历处理所有的知识点文件
        for (int i = 0; i < meta.length; ++i) {
            List<KnowledgePointTree.Node> nodes = readKnowledgePoints(meta[i][0], meta[i][1], meta[i][2], "GB2312");

            KnowledgePointTree tree = new KnowledgePointTree();
            tree.nodes.addAll(nodes);
            tree.build();
            tree.walk();

            String fileName = meta[i][1] + "-" + meta[i][2] + ".json";
            FileUtils.writeStringToFile(new File(saveDir, fileName), JSON.toJSONString(tree.nodes, true), "UTF-8");
        }
    }

    /**
     * 从 SQL Server 导出的知识点 csv 文件中读取所有的知识点
     * @param path 知识点的 csv 文件
     * @param subject     知识点的科目
     * @param subjectCode 知识点的科目编码
     * @param encoding    知识点的文件编码
     * @return 返回知识点的 list
     * @throws IOException
     */
    public static List<KnowledgePointTree.Node> readKnowledgePoints(String path, String subject, String subjectCode, String encoding) throws IOException {
        InputStream in = new FileInputStream(path);
        Reader reader = new InputStreamReader(in, encoding);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);

        List<KnowledgePointTree.Node> nodes = new LinkedList<>();
        nodes.add(new KnowledgePointTree.Node("000", subject + "-" + subjectCode, subject, subjectCode)); // 根结点

        for (CSVRecord record : records) {
            try {
                String code = record.get("KPID");
                String name = record.get("KPName");

                if ("----".equals(code)) { continue; }
                nodes.add(new KnowledgePointTree.Node(code, name, subject, subjectCode));
            } catch (Exception ex) {
                System.out.println(record.toString());
            }
        }

        return nodes;
    }
}
