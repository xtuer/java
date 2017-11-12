import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileReader;

public class XmlRead {
    public static void main(String[] args) throws Exception {
        // <book>
        //     <author name="'Biao' &amp; Alice &quot;Nio&quot; &lt;Goo&gt;" />
        // </book>
        SAXReader reader = new SAXReader();
        Document document = reader.read(new FileReader("/Users/Biao/Desktop/x.xml"));
        Element author = (Element)document.selectSingleNode("//book/author");
        System.out.println(author.attribute("name").getValue()); // 'Biao' & Alice "Nio" <Goo>
    }
}
