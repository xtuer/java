import com.xtuer.bean.User;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:elasticsearch.xml"})
public class TestWithTemplate {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    // 初始化数据
    @Test
    public void initDB() {
        // 清空 index user 中的数据
        elasticsearchTemplate.deleteIndex(User.class);

        // 如果 Index 不存在则创建
        if (!elasticsearchTemplate.indexExists(User.class)) {
            elasticsearchTemplate.createIndex(User.class);
        }

        // 如果不设置，则会使用默认分词器
        elasticsearchTemplate.putMapping(User.class); // 需要索引已经存在, 会使用 User 中的 @Field 确定分词器等

        // 插入 users 到 index user
        elasticsearchTemplate.index(new IndexQueryBuilder().withObject(new User(1, "Marjory Joyce", "我们都是中华人民共和国公民")).build());
        elasticsearchTemplate.index(new IndexQueryBuilder().withObject(new User(2, "Lamb Cook", "中华人民")).build());
        elasticsearchTemplate.index(new IndexQueryBuilder().withObject(new User(3, "Katharine Warren", "和")).build());
        elasticsearchTemplate.index(new IndexQueryBuilder().withObject(new User(4, "Dillon George", "共和国公民")).build());
        elasticsearchTemplate.index(new IndexQueryBuilder().withObject(new User(5, "Dillon George", "你和我")).build());
    }

    // 使用 username 查询
    @Test
    public void findUsersByUsername() {
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("username", "Cook")).build();
        List<User> users = elasticsearchTemplate.queryForList(query, User.class);
        System.out.println(users);
    }

    // 使用 nickname 查询
    @Test
    public void findUsersByNickname() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("nickname", "共和国公民"))
                .withPageable(PageRequest.of(0, 11))
                .build();
        List<User> users = elasticsearchTemplate.queryForList(query, User.class);
        System.out.println(users);
    }

    @Test
    public void findUsers() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("username", "Joyce"));
        boolQueryBuilder.must(QueryBuilders.matchQuery("nickname", "中华人民共和国公民"));

        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .build();

        List<User> users = elasticsearchTemplate.queryForList(query, User.class);
        System.out.println(users);
    }
}
