import com.xtuer.service.PaperImportService;
import com.xtuer.util.CommonUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Properties;

public class PaperMetaImporter {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/importer.xml");
        PaperImportService importService = context.getBean("paperImportService", PaperImportService.class);
        Properties config = context.getBean("config", Properties.class);

        // 试卷元数据: 科目和此科目的元数据文件
        List<String> infos = CommonUtils.getStrings(config, "paperMetaInfo");

        // 按学科导入试卷的元数据
        for (String info : infos) {
            String[] tokens = info.split(",");  // 高中政治, /Users/Biao/Documents/套卷/papers-original/试卷属性/高中化学.csv
            String subject = tokens[0].trim();  // 学科
            String metaFile = tokens[1].trim(); // 学科的元数据文件

            importService.updatePapersMeta(metaFile, subject); // 导入试卷的元数据
        }
    }
}
