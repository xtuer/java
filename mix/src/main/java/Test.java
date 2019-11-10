import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) throws Exception {
        JSONObject obj = JSON.parseObject(FileUtils.readFileToString(new File("/Users/Biao/Desktop/1.json"), StandardCharsets.UTF_8));
        JSONArray questions = obj.getJSONArray("data");

        for (Object q : questions) {
            JSONObject question = (JSONObject) q;
            System.out.println(question.get("qtypeCategory") + " - " + question.get("qtypeName"));
        }
    }
}
