import java.util.Map;
import java.util.TreeMap;

/**
 * 导出点考考试结果
 */
public class ExamExporter {
    public static void main(String[] args) throws Exception {
        String examineesPath = "/Users/Biao/Desktop/201607-examinees.csv"; // 报名数据的路径
        String scoresPath    = "/Users/Biao/Desktop/201607-scores.csv";
        String exportPath    = "/Users/Biao/Desktop/201607-result.csv";

        // [1]. 读取学生的报名信息
        // [2]. 读取考试成绩, 放到学生对应的课程上
        // [3]. 导出到文件
        Map<String, Examinee> examinees = ExamService.prepareExamineesInformation(examineesPath);
        ExamService.fetchScores(examinees, scoresPath);
        ExamService.exportExaminees(new TreeMap(examinees), exportPath);
    }
}
