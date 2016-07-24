import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class ExamService {
    /**
     * 准备考生信息
     *
     * @param examineesPath 报名数据的路径
     * @return
     * @throws Exception
     */
    public static Map<String, Examinee> prepareExamineesInformation(String examineesPath) throws Exception {
        Map<String, Examinee> examinees = new HashMap<String, Examinee>();

        Reader reader = new InputStreamReader(new FileInputStream(examineesPath));
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);

        for (CSVRecord record : records) {
            Examinee examinee = ExamService.findExamineeByUid(examinees, record.get("考籍号").trim());
            examinee.name = record.get("姓名").trim();

            Exam exam = ExamService.findExamByCourseCode(examinee, record.get("课程代码").trim());
            exam.courseName = record.get("课程名称").trim();
            exam.roomCode = record.get("考点代码").trim();
            exam.roomName = record.get("考点名称").trim();

            examinees.put(examinee.uid, examinee);
        }

        return examinees;
    }

    /**
     * 提取学生的考试成绩
     *
     * @param examinees 学生的 map
     * @param scoresPath 成绩文件的路径
     */
    public static void fetchScores(Map<String, Examinee> examinees, String scoresPath) throws Exception {
        Reader reader = new InputStreamReader(new FileInputStream(scoresPath));
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);

        for (CSVRecord record : records) {
            String[] tokens = record.get("阶段1").trim().split("_");
            String examUid = tokens[0]; // 考籍号
            tokens = record.get("阶段2").trim().replaceAll("[a-zA-Z]+", "").split("-");
            String courseCode = tokens[0]; // 课程编码
            int phase = Integer.parseInt(tokens[2]); // 阶段
            double score = Double.parseDouble(record.get("得分").trim()); // 成绩

            // 找到学生对应课程对应阶段的分数
            Examinee examinee = ExamService.findExamineeByUid(examinees, examUid);
            Exam exam = ExamService.findExamByCourseCode(examinee, courseCode);
            exam.courseName = record.get("课程").trim();

            // 取最高成绩
            if (score > exam.scores.get(phase)) {
                exam.scores.put(phase, score);
            }
        }

        reader.close();
    }

    /**
     * 导出成绩到文件
     *
     * @param examinees
     * @param path
     * @throws Exception
     */
    public static void exportExaminees(Map<String, Examinee> examinees, String path) throws Exception {
        StringBuilder result = new StringBuilder();
        CSVFormat csvFormat =  CSVFormat.DEFAULT.withRecordSeparator("\n");
        CSVPrinter csvPrinter = new CSVPrinter(result, csvFormat);

        // CSV 文件的列名
        csvPrinter.printRecord("姓名", "考籍号", "课程编码", "课程名称", "考点编码", "考点名称", "复习", "开放", "封闭");

        for (String uid : examinees.keySet()) {
            Examinee examinee = examinees.get(uid);

            // 按课程导出, 每门课程占用一行
            for (Map.Entry<String, Exam> entry: examinee.exams.entrySet()) {
                Exam exam = entry.getValue();
                csvPrinter.printRecord(examinee.name, examinee.uid, exam.courseCode, exam.courseName, exam.roomCode,
                        exam.roomName, exam.scores.get(1), exam.scores.get(3), exam.scores.get(2));
            }
        }

        csvPrinter.close();
        FileUtils.writeStringToFile(new File(path), result.toString(), "UTF-8");
    }

    /**
     * 查找学生, 如果找不到就创建
     * @param examinees 学生对象的 map
     * @param examineeUid 学生的唯一编号
     * @return
     */
    public static Examinee findExamineeByUid(Map<String, Examinee> examinees, String examineeUid) {
        Examinee examinee = examinees.get(examineeUid);

        if (examinee == null) {
            examinee = new Examinee(examineeUid);
            examinees.put(examineeUid, examinee);
        }

        return examinee;
    }

    /**
     * 查找学生报名的课程, 如果找不到就创建
     * @param examinee 学生对象
     * @param courseCode 课程编码
     * @return
     */
    public static Exam findExamByCourseCode(Examinee examinee, String courseCode) {
        Exam exam = examinee.exams.get(courseCode);

        if (exam == null) {
            exam = new Exam(courseCode);
            examinee.exams.put(courseCode, exam);
        }

        return exam;
    }
}
