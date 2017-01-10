import com.mzlion.easyokhttp.HttpClient;

public class OkHttpEasyTest {
    public static void main(String[] args) {
        String responseData = HttpClient.get("https://www.baidu.com").execute().asString(); // 自动处理证书信息
        System.out.println(responseData);
    }
}
