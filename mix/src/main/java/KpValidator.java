import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.Objects;

import java.io.File;
import java.io.IOException;

/**
 * 校验 kp-tree.json 中路径拼出的编码和给定的编码，输出不一致的知识点
 */
public class KpValidator {
    public static void main(String[] args) throws IOException {
        String path = System.getProperty("path", "/Users/Biao/Desktop/kp-tree.json");
        JSONObject json = JSON.parseObject(FileUtils.readFileToString(new File(path), "UTF-8"));

        treeWalk(json);
    }

    public static void treeWalk(JSONObject obj) {
        String name = obj.get("name").toString();
        String code = obj.get("code").toString();
        String originalCode = obj.get("originalCode").toString();

        // 原始编码不为空即为叶子节点，且拼出的路径编码和给定的编码不同
        if (StringUtils.isNotBlank(originalCode) && !Objects.equals(code, originalCode)) {
            System.out.println(name + ", " + code + ", " + originalCode);
        }

        // 递归遍历 children
        for (Object child : obj.getJSONArray("children")) {
            treeWalk((JSONObject) child);
        }
    }
}
