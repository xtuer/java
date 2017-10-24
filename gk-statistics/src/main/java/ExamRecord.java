import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
@Accessors(chain = true)
public class ExamRecord {
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String idNo; // 身份证号码
    private String studentName; // 学生名字
    private String card;
    private String courseCode; // 课程编码
    private String courseName; // 课程名字
    private Date   startTime;  // 打开试卷时间
    private Date   submitTime; // 提交试卷试卷
    private double score;      // 考试成绩
    private String jieFu;      // 不知道

    private Date   swipeCardTime; // 每个考试只用一个刷卡时间: 取考试前 15 分钟的有效刷卡记录
    private String name;          // 刷卡时上传的身份证上的名字

    public static Map<String, List<ExamRecord>> readExamRecords(String csvPath) throws Exception {
        Reader reader = new FileReader(csvPath);
        Iterable<CSVRecord> csvRecords = CSVFormat.EXCEL.withHeader().parse(reader);

        // 读取并创建 ExamRecord 对象
        List<ExamRecord> cardRecords = new LinkedList<>();
        for (CSVRecord record : csvRecords) {
            Date startTime  = FORMATTER.parse(record.get("开考时间（考试系统）").replace(" ", " "));
            Date submitTime = FORMATTER.parse(record.get("提交时间（考试系统）").replace(" ", " "));

            ExamRecord r = new ExamRecord();
            r.setIdNo(record.get("身份证"))
                    .setStudentName(record.get("STU_NAME"))
                    .setCard(record.get("CARD"))
                    .setCourseCode(record.get("COURSE_CODE"))
                    .setCourseName(record.get("CNAME"))
                    .setStartTime(startTime)
                    .setSubmitTime(submitTime)
                    .setScore(Double.parseDouble(record.get("分数（考试系统）")))
                    .setJieFu(record.get("JIEFU"));
            cardRecords.add(r);
        }

        // idNo -> examRecord
        Map<String, List<ExamRecord>> map = new HashMap<>();
        for (ExamRecord record : cardRecords) {
            List<ExamRecord> rs = map.computeIfAbsent(record.getIdNo(), key -> new LinkedList<>());
            rs.add(record);
        }

        map.forEach((idNo, records) -> {
            records.sort((a, b) -> {
                return a.getStartTime().before(b.getStartTime()) ? -1 : 1;
            });
        });

        return map;
    }
}
