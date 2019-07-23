import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public class TestLineCount {
    public static void main(String[] args) throws IOException {
        JSONObject json = JSON.parseObject(FileUtils.readFileToString(new File("/Users/Biao/Desktop/kp-tree.json"), "UTF-8"));

        treeWalk(json);
    }

    public static void treeWalk(JSONObject obj) {
        String name = obj.get("name").toString();
        String code = obj.get("code").toString();
        String originalCode = obj.get("originalCode").toString();

        if (!Objects.equal(code, originalCode) && StringUtils.isNotBlank(originalCode)) {
            System.out.println(name + ", " + code + ", " + originalCode);
        }

        for (Object child : obj.getJSONArray("children")) {
            treeWalk((JSONObject) child);
        }
    }
}
