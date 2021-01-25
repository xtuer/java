import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) throws Exception {
        // ssh root@192.168.1.250 "kubectl exec -ti mysql-operator-59cd7bc869-8xng9 -n mysql-operator -- ps aux -w -w | grep mysql"
        ProcessBuilder pb = new ProcessBuilder("ssh", "root@192.168.1.250", "kubectl exec -ti mysql-operator-59cd7bc869-8xng9 -n mysql-operator -- ps aux -w -w | grep mysql");
        pb.redirectErrorStream(true); // 错误的输出会 redirect 到成功的输出中
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // p.waitFor();

        System.out.println("End");
    }
}
