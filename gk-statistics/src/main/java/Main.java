import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main {
    public static int validExamCount = 0;
    public static List<ExamRecord> finalExamRecords = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        String swipeCardRecordsCsv = "/Users/Biao/Documents/workspace/Java/gk-statistics/data/刷身份证时间.csv";
        String examRecordsCsv      = "/Users/Biao/Documents/workspace/Java/gk-statistics/data/考试系统记录.csv";
        String saveCsv             = "/Users/Biao/Documents/workspace/Java/gk-statistics/data/过考统计结果.csv";

        Map<String, List<SwipeCardRecord>> studentSwipeCardRecords = SwipeCardRecord.readSwipeCardRecords(swipeCardRecordsCsv);
        Map<String, List<ExamRecord>> studentExamRecords = ExamRecord.readExamRecords(examRecordsCsv);

        studentExamRecords.forEach((idNo, examRecords) -> {
            // 遍历每一条考试记录，找到此考试前的刷卡时间
            for (ExamRecord examRecord : examRecords) {
                Date st = new Date(examRecord.getStartTime().getTime() - 1000 * 60 * 15); // 开考前 15 分钟
                Date et = new Date(examRecord.getStartTime().getTime() + 1000 * 1);       // 开考后 1 秒

                List<SwipeCardRecord> swipeCardRecord = studentSwipeCardRecords.get(idNo);

                // 找到离考试相近的刷卡记录
                SwipeCardRecord closeSwipeCardRecord = null;
                for (SwipeCardRecord cardRecord : swipeCardRecord) {
                    // 查找开考前 15 分钟和开考后 1 秒之间的刷卡记录
                    if (cardRecord.getSwipeCardTime().after(st) && cardRecord.getSwipeCardTime().before(et)) {
                        closeSwipeCardRecord = cardRecord;

                        // 如果有合法的刷卡记录，则使用此记录
                        if (!cardRecord.getName().contains("?") && !cardRecord.getName().contains("NULL")) {
                            validExamCount++;
                            break;
                        }
                    }
                }

                if (closeSwipeCardRecord != null) {
                    examRecord.setSwipeCardTime(closeSwipeCardRecord.getSwipeCardTime()).setName(closeSwipeCardRecord.getName());
                } else {
                    examRecord.setName("***"); // 找不到刷卡记录的 name 标记为 ***
                }

                finalExamRecords.add(examRecord);
            }
        });

        finalExamRecords.sort((a, b) -> -(a.getName().compareTo(b.getName())));
        saveToCsv(finalExamRecords, saveCsv);
        System.out.println("共有学生 " + studentExamRecords.size() + " 人，共有科目: " + finalExamRecords.size() + "，有效科目: " + validExamCount);
    }

    public static String format(Date date) {
        try {
            return ExamRecord.FORMATTER.format(date);
        } catch (Exception ex) {
            return "1970-00-00 00:00:00";
        }
    }

    public static String timeString(long seconds) {
        String text = null;

        if (seconds >= 3600) {
            text = (seconds / 3600) + "小时";
            seconds %= 3600;
        }

        if (seconds >= 60) {
            text = (seconds / 60) + "分" + (seconds % 60) + "秒";
        } else {
            text = seconds + "秒";
        }

        return text;
    }

    // 保存结果到 CSV 文件
    public static void saveToCsv(List<ExamRecord> examRecords, String path) throws Exception {
        // Create the CSVFormat object with "\n" as a record delimiter
        StringBuilder result = new StringBuilder();
        CSVFormat csvFormat =  CSVFormat.DEFAULT.withRecordSeparator("\n");
        CSVPrinter csvPrinter = new CSVPrinter(result, csvFormat);

        // Write headers
        csvPrinter.printRecord("身份证号码", "studentName", "刷卡时上传的名字", "card", "courseName", "courseCode",
                               "成绩", "刷卡时间", "开始考试时间", "提交试卷时间", "答卷时间", "答卷时间(秒)", "JIEFU");

        for (ExamRecord record : examRecords) {
            long duration = (record.getSubmitTime().getTime() - record.getStartTime().getTime()) / 1000; // 答题时间，单位秒

            csvPrinter.printRecord(record.getIdNo(), record.getStudentName(), record.getName(),
                    record.getCard(), record.getCourseName(), record.getCourseCode(), record.getScore(),
                    format(record.getSwipeCardTime()), format(record.getStartTime()), format(record.getSubmitTime()),
                    timeString(duration), duration, record.getJieFu());
        }
        csvPrinter.close();

        FileUtils.writeStringToFile(new File(path), result.toString(), "UTF-8");
    }
}
