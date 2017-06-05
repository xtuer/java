package com.xtuer.importer;

import com.xtuer.service.PaperImportService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PaperMetaImporter {
    // 试卷属性文件的目录
    private static final String BASE_DIR = "/Users/Biao/Documents/套卷/试卷属性(完整)/试卷属性";

    // 学科 + 此学科试卷目录
    private static final String[][] PAPERS_META = {
            {"高中化学", BASE_DIR + "/高中化学.csv"},
            {"高中历史", BASE_DIR + "/高中历史.csv"},
            {"高中地理", BASE_DIR + "/高中地理.csv"},
            {"高中数学", BASE_DIR + "/高中数学.csv"},
            {"高中物理", BASE_DIR + "/高中物理.csv"},
            {"高中生物", BASE_DIR + "/高中生物.csv"},
            {"高中语文", BASE_DIR + "/高中语文.csv"},
    };

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/importer.xml");
        PaperImportService importer = context.getBean("paperImportService", PaperImportService.class);

        for (int i = 6; i < PAPERS_META.length; ++i) {
            long start = System.currentTimeMillis() / 1000;

            String path    = PAPERS_META[i][1];
            String subject = PAPERS_META[i][0];
            importer.updatePapersMeta(path, subject);

            long end = System.currentTimeMillis() / 1000;
            System.out.println("Elapsed: " + (end - start));
        }
    }

}
