package util;

import bean.Question;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Utils {
    /**
     * 从 CSV 的知识点文件的名字中提取知识点的科目名称
     * 文件名格式为: 高中语文-GYWT033C.csv
     *
     * @param csvKpFileName CSV 的知识点文件的名字
     * @return 返回知识点的科目名称
     */
    public static String getKpSubjectName(String csvKpFileName) {
        int hyphenIndex = csvKpFileName.indexOf("-");
        return csvKpFileName.substring(0, hyphenIndex);
    }

    /**
     * 从 CSV 的知识点文件的名字中提取知识点的科目编码
     * 文件名格式为: 高中语文-GYWT033C.csv
     *
     * @param csvKpFileName CSV 的知识点文件的名字
     * @return 返回知识点的科目编码
     */
    public static String getKpSubjectCode(String csvKpFileName) {
        int hyphenIndex = csvKpFileName.indexOf("-");
        int dotIndex = csvKpFileName.indexOf(".");
        return csvKpFileName.substring(hyphenIndex+1, dotIndex);
    }

    /**
     * 查找科目编码对应的科目名称
     *
     * @param subjectCode 科目编码
     * @return 返回科目名称
     */
    public static String findSubjectName(String subjectCode) {
        return subjects.get(subjectCode);
    }

    // 科目编码和名称的 map
    public static Map<String, String> subjects = new HashMap<>();

    static {
        subjects.put("GYWT033C", "高中语文");
        subjects.put("GDLT030C", "高中地理");
        subjects.put("GLST033C", "高中历史");
        subjects.put("GZSX060B", "高中数学");
        subjects.put("GZYW033C", "高中语文");
        subjects.put("GZLS033C", "高中历史");
        subjects.put("GDLZ033C", "高中地理");
        subjects.put("GZSW033C", "高中生物");
        subjects.put("GYYK034C", "高中英语");
        subjects.put("GZDL033C", "高中地理");
        subjects.put("GZHX062A", "高中化学");
        subjects.put("GZYY033C", "高中英语");
        subjects.put("GSZH030C", "高中数学");
        subjects.put("GZZZ033C", "高中政治");
        subjects.put("GZWL061A", "高中物理");
        subjects.put("GZWL061B", "高中物理");
        subjects.put("GSWT033C", "高中生物");
        subjects.put("ZSWZ033C", "高中生物");
    }

    /**
     * 读取题目信息的 XML 文件，文件名为科目编码.xml，如 GYWT033C.xml，为每一个题目创建一个 Map 的项:
     *     key   为 subjectCode+originalId
     *     value 为 Question 对象
     *
     * @param infoDir 题目信息的 XML 文件所在目录
     * @return 题目信息的 map
     * @throws Exception 读取 XML 文件出错时抛出异常
     */
    public static Map<String, Question> prepareQuestionInfo(String infoDir) throws Exception {
        Map<String, Question> info = new HashMap<>();
        Collection<File> infoFiles = FileUtils.listFiles(new File(infoDir), new String[] {"xml"}, false);

        for (File infoFile : infoFiles) {
            System.out.println(infoFile.getName());

            Document doc = Jsoup.parseBodyFragment(FileUtils.readFileToString(infoFile, "GB2312"));
            Elements elems = doc.select("root > i");
            String subjectCode = FilenameUtils.getBaseName(infoFile.getName());

            for (Element elem : elems) {
                String originalId = elem.attr("I_ProblemID");
                String answer = elem.attr("I_Answer");
                String demand = elem.attr("I_TeachDemand");
                String knowledgePointCode = elem.attr("I_VCP").trim();
                String type = elem.attr("I_ProbType");
                int score = Integer.parseInt(elem.attr("I_Score"));
                int difficulty = Integer.parseInt(elem.attr("I_DifCoef"));

                Question question = new Question();
                question.setOriginalId(originalId).setAnswer(answer).setDemand(demand)
                        .setKnowledgePointCode(knowledgePointCode).setSubjectCode(subjectCode)
                        .setScore(score).setType(type).setDifficulty(difficulty);

                info.put(subjectCode + "-" + originalId, question);
            }
        }

        return info;
    }
}
