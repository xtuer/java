import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 刷卡记录
 */
@Getter
@Setter
@Accessors(chain = true)
public class SwipeCardRecord {
    private String idNo; // 身份证号码
    private String name; // 刷卡时传的身份证上的名字
    private String studentName; // 学生名字
    private String card;
    private Date   swipeCardTime; // 刷卡时间

    /**
     * 从文件中读取刷卡记录，保存为 Map: 身份证号码 idNo 为 key，value 为刷卡记录 SwipeCardRecord 对象
     *
     * @param csvPath 刷卡记录的文件路径
     * @return
     * @throws Exception
     */
    public static Map<String, List<SwipeCardRecord>> readSwipeCardRecords(String csvPath) throws Exception {
        Reader reader = new FileReader(csvPath);
        Iterable<CSVRecord> csvRecords = CSVFormat.EXCEL.withHeader().parse(reader);

        // 读取并创建 SwipeCardRecord 对象
        List<SwipeCardRecord> cardRecords = new LinkedList<>();
        for (CSVRecord record : csvRecords) {
            Date date = ExamRecord.FORMATTER.parse(record.get("CHECK_TIME").replace(" ", " "));
            SwipeCardRecord r = new SwipeCardRecord();
            r.setIdNo(record.get("IDENTITY_NUM"))
                    .setName(record.get("NAME"))
                    .setStudentName(record.get("STU_NAME"))
                    .setCard(record.get("CARD"))
                    .setSwipeCardTime(date);
            cardRecords.add(r);
        }

        // idNo -> cardRecord
        Map<String, List<SwipeCardRecord>> map = new HashMap<>();
        for (SwipeCardRecord record : cardRecords) {
            List<SwipeCardRecord> rs = map.computeIfAbsent(record.getIdNo(), key -> new LinkedList<>());
            rs.add(record);
        }

        map.forEach((idNo, records) -> {
            records.sort((a, b) -> {
                return a.getSwipeCardTime().before(b.getSwipeCardTime()) ? -1 : 1;
            });
        });

        return map;
    }
}
