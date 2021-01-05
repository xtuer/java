import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class Test {
    public static void main(String[] args) throws Exception {
        int[] nums = new int[] {2, 3, 4, 5, 6, 1, 7, 8};
        int min = Arrays.stream(nums).min().getAsInt();
        System.out.println(min);
    }
}
