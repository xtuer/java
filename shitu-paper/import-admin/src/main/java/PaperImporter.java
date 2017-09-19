import com.xtuer.service.PaperImportService;
import com.xtuer.util.CommonUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.List;
import java.util.Properties;

public final class PaperImporter {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/importer.xml");
        PaperImportService importService = context.getBean("paperImportService", PaperImportService.class);
        Properties config = context.getBean("config", Properties.class);

        // 试卷导入后保存到的目录
        String destDirectory = config.getProperty("paperDest");

        // 试卷的信息: 学科和试卷所在文件夹
        List<String> infos = CommonUtils.getStrings(config, "paperInfo");

        // 按学科导入试卷
        for (String info : infos) {
            String[] tokens = info.split(","); // 高中政治, /Users/Biao/Documents/套卷/original-papers/高中政治（18套）/GZZZ033C
            String subject = tokens[0].trim(); // 学科
            String paperDirectory = tokens[1].trim(); // 试卷目录
            List<File> papers = importService.listPapers(paperDirectory); // 试卷文件

            importService.importPaper(papers, destDirectory, subject); // 导入试卷
        }
    }
}
