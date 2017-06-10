import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xtuer.bean.Admin;

public class Test {
    public static void main(String[] args) throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setName("Alice");

        System.out.println(JSON.toJSONString(admin, SerializerFeature.PrettyFormat));
    }
}
