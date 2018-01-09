import bean.PaperDirectory;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.SnowflakeIdWorker;
import util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class PaperDirectoryToJson {
    private static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
    private static List<PaperDirectory> directories = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);

        String paperDocDir = config.getProperty("paperDocDir");   // 试卷的 doc 文件夹
        String paperDirectoryJson = config.getProperty("paperDirectoryJson");   // 试卷文件夹的 json 路径

        treeWalk(new File(paperDocDir), "", null, 0);

        // 输出试卷逻辑目录
        FileUtils.writeStringToFile(new File(paperDirectoryJson), JSON.toJSONString(directories, true), "UTF-8");

        // 访问所有的试卷文件
        for (PaperDirectory directory : directories) {
            String dirPath = paperDocDir + directory.getRelativePath();
            Collection<File> docs = FileUtils.listFiles(new File(dirPath), new String[] {"doc"}, false);

            for (File doc : docs) {
                // System.out.println(doc.getAbsolutePath());
            }
        }
    }

    public static void treeWalk(File dir, String relativePath, PaperDirectory parentCategory, int level) {
        PaperDirectory catalog = new PaperDirectory();
        catalog.setPaperDirectoryId("" + idWorker.nextId());

        if (level >= 1) {
            catalog.setName(dir.getName());
            catalog.setRelativePath(relativePath);
            directories.add(catalog);
        }

        if (level == 1) {
            catalog.setParentPaperDirectoryId("0");
        } else if (level > 1) {
            catalog.setParentPaperDirectoryId(parentCategory.getPaperDirectoryId());
        }

        File[] children = dir.listFiles(f -> f.isDirectory());

        for (File child : children) {
            treeWalk(child, relativePath + "/" + child.getName(), catalog, ++level);
        }
    }
}
