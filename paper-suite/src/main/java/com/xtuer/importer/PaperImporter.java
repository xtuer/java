package com.xtuer.importer;

import com.xtuer.service.PaperImportService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.List;

public final class PaperImporter {
    // 导入试卷时试卷保存的目录
    private static final File PAPER_DEST_DIR = new File("/Users/Biao/Documents/套卷/papers");

    // 试卷文件的基础目录
    private static final String BASE_DIR = "/Users/Biao/Documents/套卷";

    // 学科 + 此学科试卷目录
    private static final String[][] PAPERS_INFO = {
            {"高中语文", BASE_DIR + "/高中语文（2804套）/GZYW033C"}
    };

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/importer.xml");
        PaperImportService importer = context.getBean("paperImportService", PaperImportService.class);

        // 导入所有学科的试卷
        for (int i = 0; i< PAPERS_INFO.length; ++i) {
            String subject = PAPERS_INFO[i][0];
            List<File> papers = importer.listPapers(PAPERS_INFO[i][1]);
            importer.importPaper(papers, PAPER_DEST_DIR, subject);
        }
    }
}
