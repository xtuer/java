import bean.Paper;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.SnowflakeIdWorker;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PaperXmlToJson {
    private static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);

        String paperXmlDir = config.getProperty("paperXmlDir");   // 试卷的 Xml 文件夹
        String paperJsonDir = config.getProperty("paperJsonDir"); // 试卷的 Json 文件夹
        Collection<File> paperXmls = FileUtils.listFiles(new File(paperXmlDir), new String[]{"xml"}, false);

        for (File xml : paperXmls) {
            Document doc = Jsoup.parseBodyFragment(FileUtils.readFileToString(xml, "GB2312"));
            Elements elems = doc.select("root > p");
            Map<String, Paper> papers = new HashMap<>();

            for (Element elem : elems) {
                String originalPaperId = elem.attr("FixPaperID").trim();
                String originalName = elem.attr("FixPaperName").trim();
                String publishYear = elem.attr("PaperYear").trim();
                String region = elem.attr("PaperRegion").trim();
                String paperFrom = elem.attr("PaperFrom").trim();
                String paperType = elem.attr("PaperType").trim();

                Paper paper = new Paper();
                paper.setName(originalName).setOriginalPaperId(originalPaperId).setPublishYear(publishYear)
                        .setRegion(region).setPaperFrom(paperFrom).setPaperType(paperType);
                papers.put(paper.getName(), paper);
            }

            String filename = FilenameUtils.getBaseName(xml.getName());
            String path = paperJsonDir + "/" + filename + ".json";
            FileUtils.writeStringToFile(new File(path), JSON.toJSONString(papers), "UTF-8");
        }
    }
}
