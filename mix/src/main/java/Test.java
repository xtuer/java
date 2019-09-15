import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Test {
    public static void main(String[] args) throws Exception {
        String json = FileUtils.readFileToString(new File("/Users/Biao/Desktop/化学-all.json"), StandardCharsets.UTF_8);
        JSONObject root = JSON.parseObject(json);
        // breadthFirstTravelKp(root);

        // [2] 使用过滤器
        Path dir = Paths.get("/Users/Biao/Desktop");
        Files.list(dir).filter(path -> path.toString().endsWith("-all.json")).forEach(path -> {
            System.out.println(path.toFile());
        });
    }

    // 深度遍历
    public static void walkKp(JSONObject kp) {
        System.out.println(kp.getString("name") + ", " + kp.getString("code"));

        JSONArray children = kp.getJSONArray("children");
        int len = children.size();

        for (int i = 0; i < len; i++) {
            walkKp(children.getJSONObject(i));
        }
    }

    // 广度遍历
    public static void breadthFirstTravelKp(JSONObject root) {
        LinkedList<JSONObject> queue = new LinkedList<>();
        queue.offer(root);
        String subject = root.getString("code"); // 学科编码
        System.out.println("学科: " + subject);

        while (!queue.isEmpty()) {
            JSONObject kp = queue.poll();
            System.out.println(kp.getString("name") + ", " + kp.getString("code"));

            JSONArray children = kp.getJSONArray("children");
            int len = children.size();

            for (int i = 0; i < len; i++) {
                queue.offer(children.getJSONObject(i));
            }
        }
    }
}
