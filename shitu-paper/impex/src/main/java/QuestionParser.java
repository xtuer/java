import bean.Constants;
import bean.Question;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.SnowflakeIdWorker;
import util.Utils;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * 根据题目的 HTML 文件和 XML 信息文件，解析题目信息保存为 json 文件:
 * 1. 遍历题目 HTML 文件
 * 2. 使用 Jsoup 读取 HTML 文件
 * 3. 抽取题目题干
 * 4. 修改题干中的图片路径: /question-image/科目编码/图片名字.jpg，例如 /question-image/GDLT030C/GDLT030C_030C000927_Aimage002.jpg
 * 5. 抽取题目解析
 * 6. 上面得到的信息保存为 json 文件: 题目ID.json
 */
public class QuestionParser {
    private static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
    private String question;
    private String subjectCode;

    /**
     * 使用题目的 HTML 内容和科目编码初始化解析器
     *
     * @param question    题目的内容
     * @param subjectCode 科目的编码
     */
    public QuestionParser(String question, String subjectCode) {
        this.question = question;
        this.subjectCode = subjectCode;
    }

    public Question parse() {
        Question q = new Question();
        q.setId(idWorker.nextId());
        q.setContent(getQuestionContent());
        q.setAnalysis(getQuestionAnalysis());
        q.setSubjectCode(subjectCode);

        return q;
    }

    /**
     * 获取题目的题干
     *
     *  @return 返回题目的题干
     */
    public String getQuestionContent() {
        int questionStartIndex = question.indexOf(Constants.QUESTION_START) + Constants.QUESTION_START.length();
        int questionEndIndex   = question.indexOf(Constants.ANALYSIS_START);

        if (questionStartIndex == -1 || questionEndIndex == -1) {
            return "";
        }

        String content = question.substring(questionStartIndex, questionEndIndex);
        Document doc = Jsoup.parseBodyFragment(content);
        Elements imgs = doc.select("img");
        for (Element img : imgs) {
            String imgPath = img.attr("src");
            img.attr("src", "/question-img/" + subjectCode + "/" + imgPath);
        }

        return doc.body().html();
    }

    /**
     * 抽取题目的解析
     *
     * @return 返回题目的解析
     */
    public String getQuestionAnalysis() {
        int analysisStartIndex = question.indexOf(Constants.ANALYSIS_START) + Constants.ANALYSIS_START.length();
        int analysisEndIndex   = question.indexOf(Constants.CLOSE_START);

        if (analysisStartIndex == -1 || analysisEndIndex == -1) {
            return "";
        }

        String analysis = question.substring(analysisStartIndex, analysisEndIndex);
        Document doc = Jsoup.parseBodyFragment(analysis);
        Elements imgs = doc.select("img");
        for (Element img : imgs) {
            String imgPath = img.attr("src");
            img.attr("src", "/question-img/" + subjectCode + "/" + imgPath);
        }

        return doc.body().html();
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        Properties config = context.getBean("config", Properties.class);

        String questionHtmlDir = config.getProperty("questionHtmlDir"); // 题目的 HTML 文件夹
        String questionInfoDir = config.getProperty("questionInfoDir"); // 题目信息的 XML 文件夹
        String questionJsonDir = config.getProperty("questionJsonDir"); // 题目的 JSON 文件夹

        Map<String, Question> info = Utils.prepareQuestionInfo(questionInfoDir); // 题目的信息
        File[] subjectDirs = new File(questionHtmlDir).listFiles((d) -> d.isDirectory()); // 科目文件夹

        // 按科目遍历题目
        for (File subjectDir : subjectDirs) {
            String subjectCode = subjectDir.getName(); // 科目编码
            Collection<File> questionFiles = FileUtils.listFiles(subjectDir, new String[] {"html"}, false); // 所有的题目文件

            for (File questionFile : questionFiles) {
                System.out.println(subjectCode + ": " + questionFile.getName());

                // 读取题目并解析
                String questionOriginalId = FilenameUtils.getBaseName(questionFile.getName()); // 题目原始 ID
                String questionText = FileUtils.readFileToString(questionFile, "GB2312"); // 读取题目内容
                Question question   = new QuestionParser(questionText, subjectCode).parse();
                question.setOriginalId(questionOriginalId);

                // 设置题目的额外信息，例如答案、分值等
                Question infoQ = info.get(subjectCode + "-" + questionOriginalId);
                if (infoQ != null) {
                    question.setAnswer(infoQ.getAnswer()).setDemand(infoQ.getDemand())
                            .setKnowledgePointCode(infoQ.getKnowledgePointCode())
                            .setType(infoQ.getType()).setScore(infoQ.getScore())
                            .setDifficulty(infoQ.getDifficulty());
                }

                // 保存 question 到文件
                File jsonSubjectDir = new File(questionJsonDir, subjectCode);
                String filename = questionOriginalId + ".json";
                FileUtils.writeStringToFile(new File(jsonSubjectDir, filename), JSON.toJSONString(question, true), "UTF-8");
            }
        }
    }
}
