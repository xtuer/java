import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class Test {
    private int id;

    public static void main(String[] args) throws Exception {
        String data = "callback( {\"client_id\":\"101292272\",\"openid\":\"4584E3AAABFC5F052971C278790E9FCF\"} );";
        int start = data.indexOf("{");
        int end = data.lastIndexOf("}") + 1;
        String json = data.substring(start, end);

        String openId = JSON.parseObject(json).getString("openid");
        System.out.println(openId);
    }
}
