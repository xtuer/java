import com.xtuer.bean.Person;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

public class SolrTest {
    private static final String BASE_URL = "http://localhost:8983/solr/ebag";
    private SolrClient solr;

    @Before
    public void setUp() {
        solr = new HttpSolrClient(BASE_URL); // 连接断开会自动重连
    }

    /**
     * 插入 document, 其 id 为 10000
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void insert() throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.setField("id", 10000); // 如果没有指定 id 属性, 则 Solr 会自动创建一个
        document.setField("name", "Alice");
        document.setField("age", 23);

        solr.add(document);
        solr.commit();
    }

    /**
     * 使用 bean 的方式创建或者更新 document:
     *      如果有存在 document 的 id 和输入的 id 相同, 则删除 document, 然后创建
     *      如果没有, 则创建
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void insertBean() throws IOException, SolrServerException {
        Person p1 = new Person("person-10001", "Bob", 34);
        Person p2 = new Person("person-10002", "John", 88);
        Person p3 = new Person("person-10003", "Alice", 88);
        Person p4 = new Person("person-10004", "公孙二狗", 188);
        Person p5 = new Person("person-10005", "慕容二狗", 288);
        Person p6 = new Person("person-10006", "慕容狗蛋", 288);
        Person p7 = new Person("person-10007", "大上海滩 text", 288);
        Person p8 = new Person("person-10008", "一种是配置 TokenizerFactory 类", 288);

        solr.addBean(p1);
        solr.addBean(p2);
        solr.addBean(p3);
        solr.addBean(p4);
        solr.addBean(p5);
        solr.addBean(p6);
        solr.addBean(p7);
        solr.addBean(p8);
        solr.commit();
    }

    /**
     * 删除 id 为 10000 的 document
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void delete() throws IOException, SolrServerException {
        solr.deleteById("10000");
        solr.commit();
    }

    @Test
    public void select() throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        query.set("wt", "json"); // 设置查询参数
        query.set("q", "name:Alice");
        QueryResponse response = solr.query(query);
        SolrDocumentList documents = response.getResults();

        System.out.println("Num Found: " + documents.getNumFound());

        for (SolrDocument document : documents) {
            System.out.println("---------------------------------------");

            Collection<String> fieldNames = document.getFieldNames();

            for (String fieldName : fieldNames) {
                System.out.println(fieldName + " : " + document.get(fieldName));
            }
        }
    }
}
