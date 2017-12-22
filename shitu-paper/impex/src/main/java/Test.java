import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class Test {
    public static void main(String[] args) throws Exception {
        // String subjectCode = "GZWL061A";
        Document doc = Jsoup.parseBodyFragment(FileUtils.readFileToString(new File("/Users/Biao/Desktop/question/info/GZWL061A.xml"), "GB2312"));
        Elements elems = doc.select("root > i");

        for (Element elem : elems) {
            System.out.println(elem.attr("I_VCP").isEmpty());
        }
    }
}
