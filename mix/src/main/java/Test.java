import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xtuer.bean.Admin;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) throws Exception {
        Admin admin = new Admin();
        admin.setName("Alice");
        admin.setDate(new Date());

        System.out.println(JSON.toJSONString(admin, SerializerFeature.UseISO8601DateFormat));
    }
}
