import bean.Paper;
import bean.PaperDirectory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.SnowflakeIdWorker;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PaperToJson {
    private static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);

        String paperDocDir = config.getProperty("paperDocDir");   // 试卷的 doc 文件夹
        String paperDocFinalDir = config.getProperty("paperDocFinalDir");   // 试卷的 doc 文件夹
        String paperDirectoryJson = config.getProperty("paperDirectoryJson"); // 试卷的目录文件
        String paperJsonDir = config.getProperty("paperJsonDir"); // 试卷的 Json 文件夹
        String paperJson = config.getProperty("paperJson"); // 试卷的 Json 文件

        List<PaperDirectory> directories = paperDirectories(paperDirectoryJson);
        Map<String, Paper> paperInfo = paperInfo(paperJsonDir);
        List<Paper> papers = new LinkedList<>();
        System.out.println(paperInfo.size());

        // 遍历试卷目录，读取每一个试卷，移动试卷到对应的目录并修改其名字为 uuidName
        for (PaperDirectory directory : directories) {
            String realDirectory = paperDocDir + directory.getRelativePath();
            // System.out.println(realDirectory);

            // 获取学科
            int slashPos = directory.getRelativePath().indexOf("/", 1);
            slashPos = (slashPos == -1) ? directory.getRelativePath().length() : slashPos;
            String subject = directory.getRelativePath().substring(1, slashPos);
            // System.out.println(subject);

            // 获取此目录下的所有试卷 doc 文件
            for (File doc : FileUtils.listFiles(new File(realDirectory), new String[] {"doc", "docx"}, false)) {
                String paperName = FilenameUtils.getBaseName(doc.getName()).trim();
                String extension = FilenameUtils.getExtension(doc.getName());
                System.out.println(paperName);

                // 生成试卷的基础信息
                String uuid = idWorker.nextId() + "";
                Paper paper = new Paper();
                paper.setName(paperName);
                paper.setOriginalName(paperName);
                paper.setPaperDirectoryId(directory.getPaperDirectoryId());
                paper.setPaperId(uuid);
                paper.setUuidName(uuid + "." + extension);
                paper.setSubject(subject);
                papers.add(paper);

                // 完善试卷的数据库信息
                Paper temp = paperInfo.get(paperName);
                if (temp != null) {
                    paper.setOriginalPaperId(temp.getOriginalPaperId());
                    paper.setPaperFrom(temp.getPaperFrom());
                    paper.setPaperType(temp.getPaperType());
                    paper.setPublishYear(temp.getPublishYear());
                    paper.setRegion(temp.getRegion());
                }

                // 复制 doc 文件到指定目录 paper.getUuidName()
                FileUtils.copyFile(doc, new File(paperDocFinalDir + "/" + paper.getUuidName()));
            }
        }

        FileUtils.writeStringToFile(new File(paperJson), JSON.toJSONString(papers), "UTF-8");
        System.out.println("处理文件共: " + papers.size());
    }

    public static Map<String, Paper> paperInfo(String paperJsonDir) throws IOException {
        Map<String, Paper> papers = new HashMap<>();
        Collection<File> jsonFiles = FileUtils.listFiles(new File(paperJsonDir), new String[]{"json"}, false);

        for (File file : jsonFiles) {
            String json = FileUtils.readFileToString(file, "UTF-8");
            Map<String, Paper> temp = JSON.parseObject(json, new TypeReference<Map<String, Paper>>() {
            });

            if (!temp.isEmpty()) {
                papers.putAll(temp);
            }
        }

        return papers;

    }

    public static List<PaperDirectory> paperDirectories(String paperDirectoryJson) throws IOException {
        String directoriesJson = FileUtils.readFileToString(new File(paperDirectoryJson), "UTF-8");
        List<PaperDirectory> directories = JSON.parseObject(directoriesJson, new TypeReference<List<PaperDirectory>>(){});
        return directories;
    }
}
