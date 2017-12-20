import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 下载乐教乐学的试题
 */
public class QuestionDownloader {
    public static final String BASE_URL = "http://192.168.10.243/Exe/makingpaper.dll?CMD_ShowProblemContent&";// strSubject=GSZH030C&strProblemID=030C014553

    public static void main(String[] args) throws IOException {
        String[][] meta = {
                // subject, idFile, saveDir
                {"GSZH030C", "F:/题目ID/数学/GSZH030C.csv", "F:/题目/数学/GSZH030C"},
                {"GZSX060B", "F:/题目ID/数学/GZSX060B.csv", "F:/题目/数学/GZSX060B"},
                {"GZLS033C", "F:/题目ID/历史/GZLS033C.csv", "F:/题目/历史/GZLS033C"},
                {"GLST033C", "F:/题目ID/历史/GLST033C.csv", "F:/题目/历史/GLST033C"},
                {"GZHX062A", "F:/题目ID/化学/GZHX062A.csv", "F:/题目/化学/GZHX062A"},
                {"GYWT033C", "F:/题目ID/语文/GYWT033C.csv", "F:/题目/语文/GYWT033C"},
                {"GZYW033C", "F:/题目ID/语文/GZYW033C.csv", "F:/题目/语文/GZYW033C"},
                {"GZZZ033C", "F:/题目ID/政治/GZZZ033C.csv", "F:/题目/政治/GZZZ033C"},
                {"GZYY033C", "F:/题目ID/英语/GZYY033C.csv", "F:/题目/英语/GZYY033C"},
                {"GYYK034C", "F:/题目ID/英语/GYYK034C.csv", "F:/题目/英语/GYYK034C"},
                {"GDLZ033C", "F:/题目ID/地理/GDLZ033C.csv", "F:/题目/地理/GDLZ033C"},
                {"GDLT030C", "F:/题目ID/地理/GDLT030C.csv", "F:/题目/地理/GDLT030C"},
                {"GZDL033C", "F:/题目ID/地理/GZDL033C.csv", "F:/题目/地理/GZDL033C"},
                {"ZSWZ033C", "F:/题目ID/生物/ZSWZ033C.csv", "F:/题目/生物/ZSWZ033C"},
                {"GZSW033C", "F:/题目ID/生物/GZSW033C.csv", "F:/题目/生物/GZSW033C"},
                {"GSWT033C", "F:/题目ID/生物/GSWT033C.csv", "F:/题目/生物/GSWT033C"},
                {"GZWL061B", "F:/题目ID/物理/GZWL061B.csv", "F:/题目/物理/GZWL061B"},
                {"GZWL061A", "F:/题目ID/物理/GZWL061A.csv", "F:/题目/物理/GZWL061A"},
        };

        for (int i = 0; i < meta.length; ++i) {
            String subject = meta[i][0];
            String idFile  = meta[i][1];
            String saveDir = meta[i][2];

            downloadQuestions(subject, idFile, saveDir);
        }
    }

    /**
     * 下载科目 subject 下的题目，题目的 ID 保存在 idFile 中，每行一个 ID，下载后的文件保存到 saveDir
     *
     * @param subject 科目
     * @param idFile  题目 ID 文件
     * @param saveDir 保存目录
     * @throws IOException
     */
    public static void downloadQuestions(String subject, String idFile, String saveDir) throws IOException {
        // 读取试题的 ID
        List<String> questionIds = new LinkedList<>(FileUtils.readLines(new File(idFile), "UTF-8"));

        // 下载试题的逻辑:
        // 0. 如果 questionIds 不为空则进行下载
        // 1. 从 questionIds 中获取并移除第一个 questionId
        // 2. questionId 有 2 中格式: 030C000012 和 030C000012:3 (3 为尝试次数)
        // 3. 计算尝试次数和正确的 questionId
        // 4. 当尝试次数小于 5 次时下载试题
        // 5. 如果下载失败则添加到 questionIds 中继续尝试
        while (!questionIds.isEmpty()) {
            String questionId = questionIds.get(0).trim();
            questionIds.remove(0);
            if (questionId.isEmpty()) { continue; }

            int index = questionId.indexOf(":");
            int count = 1;

            if (index != -1) {
                count = Integer.parseInt(questionId.substring(index + 1)) + 1;
                questionId  = questionId.substring(0, index);
            }

            try {
                // 每个问题下载最多尝试 5 次
                if (count <= 5) {
                    // questionId = "030C014553";
                    downloadQuestionPage(subject, questionId, saveDir);
                }
            } catch (Exception ex) {
                // 下载失败，ID 添加到 questionIds 尝试再次进行下载
                String tryQuestionId = questionId + ":" + count;
                questionIds.add(tryQuestionId);
                System.out.println("Try: " + tryQuestionId);
            }
        }
    }

    /**
     * 下载题目
     *
     * @param subject    科目
     * @param questionId 题目 ID
     * @param saveDir    保存目录
     * @throws IOException
     */
    public static void downloadQuestionPage(String subject, String questionId, String saveDir) throws IOException {
        saveDir = saveDir + "/" + Math.abs(questionId.hashCode() % 100);

        // 下载网页
        String url = BASE_URL + "strSubject=" + subject + "&strProblemID=" + questionId;
        Document doc = Jsoup.connect(url).get();

        // 下载图片
        Elements imgs = doc.getElementsByTag("img");
        for (int i = 0; i < imgs.size(); ++i) {
            Element img = imgs.get(i);
            String imgUrl = img.absUrl("src");
            String imgName = FilenameUtils.getName(imgUrl);
            String path = saveDir + "/" + imgName;

            downloadImage(imgUrl, path); // 下载图片
        }

        String html = doc.toString();
        FileUtils.writeStringToFile(new File(saveDir + "/" + questionId + ".html"), html, "UTF-8");
    }

    /**
     * 使用 HttpClient 的 FluentApi 下载文件
     * downloadFile("http://xtuer.github.io/img/dog.png", "/Users/Biao/Desktop/a.png"); // 下载图片
     *
     * @param url  文件的 url
     * @param path 本地存储路径
     * @throws IOException 如果 url 的文件找不到，超时等会抛出异常
     */
    public static void downloadImage(String url, String path) throws IOException {
        byte[] content = Request.Get(url).connectTimeout(5000).socketTimeout(5000).execute().returnContent().asBytes();
        FileUtils.writeByteArrayToFile(new File(path), content);
    }
}
