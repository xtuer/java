import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupTest {
    public static void main(String[] args) {
        String html="<div>My Image <img src=\"fox.png\"></div>";
        Document doc = Jsoup.parse(html);

        Elements imgs = doc.select("img");

        for (Element img : imgs) {
            String src = img.attr("src");
            img.attr("src", "/question-img/1234/" + src); // 修改图片的路径
        }

        System.out.println(doc.body().html());
    }
}
