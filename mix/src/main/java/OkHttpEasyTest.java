import com.mzlion.easyokhttp.HttpClient;

public class OkHttpEasyTest {
    public static void main(String[] args) throws Exception {
        // 调用 https() 则信任所有证书
        String responseData = HttpClient.get("https://www.baidu.com/").execute().asString(); // 自动处理 CA 签发的证书
        System.out.println(responseData);
    }
}
