import com.alibaba.fastjson.JSONObject;
import com.mzlion.easyokhttp.HttpClient;

import java.io.IOException;

/**
 * 使用百度的 OCR 服务识别图片中的文字
 */
public class BaiduOcr {
    private static final String APP_ID  = "k364IHWiCdW1gZtL6eKfNRqM";
    private static final String APP_KEY = "2sGL1HlcoYDaStLiCrsEiNRqHbDQEWax";
    private static final String ACCESS_TOKEN = "24.2da4a08006f7b05caa2c6becbae001bb.2592000.1575536664.282335-17699862"; // 30 天有效期

    public static void main(String[] args) throws IOException {
        String json = detectText("/Users/Biao/Desktop/y.png");
        System.out.println(json);
    }

    // 识别图中的文字，返回识别结果的 JSON 格式字符串, path 为图片的路径
    public static String detectText(String path) throws IOException {
        String image = ImageBase64Utils.imageToBase64String(path);
        int startIndex = image.indexOf(",") + 1;
        image = image.substring(startIndex); // 图片的 Base64 编码是不包含图片头的，如（data:image/jpg;base64,）

        // String response = HttpClient.post("https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic")
        String response = HttpClient.post("https://aip.baidubce.com/rest/2.0/ocr/v1/formula")
                .param("access_token", ACCESS_TOKEN)
                .param("image", image)
                .execute().asString();
        return response;
    }

    // 使用 APP_ID + APP_KEY 换取 access token
    public static String requestAccessToken() {
        String response = HttpClient.post("https://aip.baidubce.com/oauth/2.0/token")
                .param("grant_type", "client_credentials")
                .param("client_id", APP_ID)
                .param("client_secret", APP_KEY)
                .execute().asString();
        JSONObject tokenObject = JSONObject.parseObject(response);
        return tokenObject.get("access_token").toString();
    }
}
