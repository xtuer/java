import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class ProtoTest {
    public static void main(String[] args) {
        MongoClient client = new MongoClient("localhost", 27017);
        MongoDatabase database = client.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("foo");

        insert(collection);
        find(collection);

        update(collection);
        find(collection);

        delete(collection);
        find(collection);
    }

    // 创建
    public static void insert(MongoCollection<Document> collection) {
        Document document = new Document()
                .append("username", "Alice")
                .append("password", "Secret")
                .append("age", 23)
                .append("rate", 652);
        collection.insertOne(document); // insertMany()
    }

    // 删除
    public static void delete(MongoCollection<Document> collection) {
        collection.deleteOne(Filters.eq("username", "Alice")); // deleteMany()
    }

    // 查询
    public static void find(MongoCollection<Document> collection) {
        FindIterable<Document> docs = collection.find(Filters.eq("username", "Alice"));
        for (Document doc : docs) {
            System.out.println(doc.getInteger("rate"));
        }
    }

    // 更新
    public static void update(MongoCollection<Document> collection) {
        collection.updateOne(Filters.eq("username", "Alice"),
                new Document("$set", new Document("rate", 1000))); // updateMany()
    }
}
