import org.apache.http.Consts;
import org.apache.http.client.fluent.Request;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadUtil {
    /**
     * 使用 HttpClient 的 FluentApi 下载文件
     * @param url 文件的 url
     * @param localPath 本地存储路径
     * @throws IOException 如果 url 的文件找不到，超时等会抛出异常
     */
    public static void downloadFile(String url, String localPath) throws IOException {
        byte[] content = Request.Get(url).connectTimeout(5000).socketTimeout(5000).execute().returnContent().asBytes();
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(localPath)));
        out.write(content);
        out.flush();
        out.close();
    }

    public static void loadProductDetails(String url) throws IOException {
        String content = Request.Get(url).connectTimeout(5000).socketTimeout(5000).execute().returnContent().asString(Consts.UTF_8);
        System.out.println(content);
    }

    public static void main(String[] args) throws IOException {
//        downloadFile("http://xtuer.github.io/img/dog.png", "/Users/Biao/Desktop/a.png"); // 下载图片
        loadProductDetails("http://www.qtdebug.com");
    }
}
