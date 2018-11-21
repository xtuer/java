import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 解析读取教材信息，获取所有的章节插入到数据库:
 * 1. 读取 books.json 得到教材信息
 * 2. 读取每本教材的章节
 * 3. 把章节插入到数据库
 */
@Getter
@Setter
public class BookMetaDataParser {
    private String directory;

    public BookMetaDataParser() {
    }

    public static void main(String[] args) throws IOException {
        String directory = "/Users/Biao/Documents/workspace/Qt/BookEditor/bin/data"; // Configurable
        BookMetaDataParser parser = new BookMetaDataParser();
        parser.setDirectory(directory);

//        List<Chapter> chapters = parser.loadChapters();
//        // 插入章节信息到数据库
//        for (Chapter chapter : chapters) {
//            System.out.println(JSON.toJSONString(chapter));
//        }

        List<KnowledgePoint> kps = parser.loadKnowledgePoints();
        for (KnowledgePoint kp : kps) {
            System.out.println(JSON.toJSONString(kp));
        }
    }

    /**
     * 加载所有教材的所有章节
     *
     * @return 返回章节列表
     * @throws IOException 读取文件异常
     */
    public List<Chapter> loadChapters() throws IOException {
        // 1. 读取教材到数组 books
        // 2. 读取每本教材的章节添加到数组 chapters

        // [1] 读取教材到数组 books
        List<Book>    books    = readBooks();
        List<Chapter> chapters = new LinkedList<>();

        // [2] 读取每本教材的章节添加到数组 chapters
        for (Book book : books) {
            List<Chapter> chapterList = readChapters(book.getCode());
            chapters.addAll(chapterList);
        }

        return chapters;
    }

    /**
     * 加载所有的知识点
     *
     * @return 返回知识点列表
     */
    public List<KnowledgePoint> loadKnowledgePoints() throws IOException {
        // 1. 读取 kps.json 得到学段和学科编码，拼出知识点编码的文件名
        // 2. 读取知识点到数组 kps
        String content     = FileUtils.readFileToString(new File(kpsDirectory(), "kps.json"), StandardCharsets.UTF_8);
        JSONObject root    = JSON.parseObject(content);
        JSONArray phases   = root.getJSONArray("phases");
        JSONArray subjects = root.getJSONArray("subjects");
        List<KnowledgePoint> kps = new LinkedList<>();

        for (int i = 0; i < phases.size(); ++i) {
            String phaseCode = phases.getJSONObject(i).getString("code");

            for (int j = 0; j < subjects.size(); ++j) {
                String subjectCode = subjects.getJSONObject(j).getString("code");
                String kpFilename = phaseCode + "-" + subjectCode + ".json"; // 知识点文件名
                kps.addAll(readKnowledgePoints(kpFilename));
            }
        }

        return kps;
    }

    /**
     * 读取教材信息
     *
     * @return 返回教材信息列表
     * @throws IOException 读取文件出错抛出异常
     */
    private List<Book> readBooks() throws IOException {
        // 所有教材的信息存储在 books.json 文件中，有 4 层：学段 > 学科 > 版本 > 教材
        // 所以需要使用 4 个嵌套 for 循环读取按层读取

        List<Book> bookList = new LinkedList<>();
        String     content  = FileUtils.readFileToString(new File(booksDirectory(), "books.json"), StandardCharsets.UTF_8);
        JSONArray  phases   = JSON.parseObject(content).getJSONArray("phases");

        // phases > subjects > versions > books: [book (title, code)]
        for (int i = 0; phases != null && i < phases.size(); ++i) {            // [1] 学段
            JSONArray subjects = phases.getJSONObject(i).getJSONArray("subjects");

            for (int j = 0; subjects != null && j < subjects.size(); ++j) {    // [2] 学科
                JSONArray versions = subjects.getJSONObject(j).getJSONArray("versions");

                for (int m = 0; versions != null && m < versions.size(); ++m) { // [3] 版本
                    JSONArray books = versions.getJSONObject(m).getJSONArray("books");

                    for (int n = 0; books != null && n < books.size(); ++n) {   // [4] 教材
                        JSONObject book  = books.getJSONObject(n);
                        String bookCode  = book.getString("code");
                        String bookTitle = book.getString("title");
                        bookList.add(new Book(bookTitle, bookCode));
                    }
                }
            }
        }

        return bookList;
    }

    /**
     * 读取编码为传入的 bookCode 的教材章节
     *
     * @param bookCode  教材编码
     * @return 返回章节信息列表
     */
    private List<Chapter> readChapters(String bookCode) throws IOException {
        List<Chapter> chapterList = new LinkedList<>();
        String        content     = FileUtils.readFileToString(new File(booksDirectory(), bookCode + ".json"), StandardCharsets.UTF_8);
        JSONArray     chapters    = JSON.parseObject(content).getJSONArray("chapters");

        // chapters: [chapter (code, title, children)]
        for (int i = 0; chapters != null && i < chapters.size(); ++i) {
            readChapter(bookCode, chapters.getJSONObject(i), chapterList);
        }

        return chapterList;
    }

    /**
     * 读取教材 bookCode 的章节 chapter 信息到数组 chapters 中
     *
     * @param bookCode 教材编码
     * @param chapter  章节对象
     * @param chapters 章节集合
     */
    private void readChapter(String bookCode, JSONObject chapter, List<Chapter> chapters) {
        // 1. 先读取当前章节
        // 2. 如果有子章节，递归读取子章节

        String code  = chapter.getString("code");
        String title = chapter.getString("title");
        chapters.add(new Chapter(title, code, bookCode));

        // 读取子章节
        JSONArray subChapters = chapter.getJSONArray("children");
        for (int i = 0; subChapters != null && i < subChapters.size(); ++i) {
            readChapter(bookCode, subChapters.getJSONObject(i), chapters);
        }
    }

    /**
     * 读取文件中的知识点
     *
     * @param knowledgePointFilename 知识点文件名
     * @return 返回知识点列表
     * @throws IOException IO 错误
     */
    private List<KnowledgePoint> readKnowledgePoints(String knowledgePointFilename) throws IOException {
        // 如果知识点的文件内容
        // 因为知识点学段和学科是编码写死的，拼出来的知识点文件有可能不存在
        File kpFile = new File(kpsDirectory(), knowledgePointFilename);
        if (!kpFile.exists()) { return Collections.emptyList(); }

        List<KnowledgePoint> kpList = new LinkedList<>();
        String content = FileUtils.readFileToString(kpFile, StandardCharsets.UTF_8);
        JSONArray kps = JSON.parseObject(content).getJSONArray("kps");

        for (int i = 0; i < kps.size(); ++i) {
            readKnowledgePoint(kps.getJSONObject(i), kpList);
        }

        return kpList;
    }

    /**
     * 读取知识点
     *
     * @param knowledgePoint  当前知识点
     * @param knowledgePoints 知识点列表
     */
    private void readKnowledgePoint(JSONObject knowledgePoint, List<KnowledgePoint> knowledgePoints) {
        // 1. 先读取当前知识点
        // 2. 如果有子知识点，递归读取子知识点
        String code  = knowledgePoint.getString("code");
        String title = knowledgePoint.getString("title");
        knowledgePoints.add(new KnowledgePoint(title, code));

        // 读取子知识点
        JSONArray subKnowledgePoints = knowledgePoint.getJSONArray("children");
        for (int i = 0; subKnowledgePoints != null && i < subKnowledgePoints.size(); ++i) {
            readKnowledgePoint(subKnowledgePoints.getJSONObject(i), knowledgePoints);
        }
    }

    // 返回教材所在目录
    private File booksDirectory() {
        return new File(directory, "books");
    }

    // 返回知识点所在目录
    private File kpsDirectory() {
        return new File(directory, "kps");
    }

    /**
     * 教材
     */
    @Getter
    @Setter
    public static class Book {
        private String title; // 教材名称
        private String code;  // 教材编码

        public Book(String title, String code) {
            this.title = title;
            this.code  = code;
        }
    }

    /**
     * 章节
     */
    @Getter
    @Setter
    public static class Chapter {
        private String title;    // 章节名称
        private String code;     // 章节编码
        private String bookCode; // 教材编码

        public Chapter(String title, String code, String bookCode) {
            this.title = title;
            this.code  = code;
            this.bookCode = bookCode;
        }
    }

    /**
     * 知识点
     */
    @Getter
    @Setter
    public static class KnowledgePoint {
        private String title; // 知识点名称
        private String code;  // 知识点编码

        public KnowledgePoint(String title, String code) {
            this.title = title;
            this.code  = code;
        }
    }
}
