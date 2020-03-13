import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Test {
    public static void main(String[] args) throws IOException {
        String url = "http://efstream.edu-edu.com.cn/234385bffff04f64bf5078230e29109d/covers/b885b2517c5349baaac40dc79b7fcf45-00002.jpg?auth_key=1583989464-522ceedb70f645ab8fbd96f5bf9f824d-0-f38e9cc44f32370ee40af4a0d434c56a";
        URL url2 = new URL(url);
        System.out.println(FilenameUtils.getName(url2.getPath()));
        // OkHttpClient client = new OkHttpClient();
        // Request request = new Request.Builder().url("http://efstream.edu-edu.com.cn/234385bffff04f64bf5078230e29109d/covers/b885b2517c5349baaac40dc79b7fcf45-00002.jpg?auth_key=1583989464-522ceedb70f645ab8fbd96f5bf9f824d-0-f38e9cc44f32370ee40af4a0d434c56a").build();
        // Response response = client.newCall(request).execute();
        //
        // if (!response.isSuccessful()) {
        //     System.out.println("Error: " + response);
        //     return;
        // }
        // //
        // FileUtils.writeByteArrayToFile(new File("/Users/Biao/Desktop/" + FilenameUtils.getExtension(url2.getPath())), response.body().bytes());
        // // System.out.println(222);
    }
}
