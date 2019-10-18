package exam;

import com.csvreader.CsvReader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;
import util.Utils;


import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExamImport {
    private static final PasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static String passwordByBCrypt(String rawPassword) {
        // return "{bcrypt}" + B_CRYPT_PASSWORD_ENCODER.encode(rawPassword);
        return DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) throws Exception {
        try {
            // 准备六个筐，暂存报数数据，然后批量存入数据库 examinee, subject, examUnit, examSite, examRoom, enrollment
            Map<String, Examinee> examineeMap = new HashMap<>();
            Map<String, Subject>  subjectMap  = new HashMap<>();
            Map<String, ExamUnit> unitMap     = new HashMap<>();
            Map<String, ExamSite> siteMap     = new HashMap<>();
            Map<String, ExamRoom> roomMap     = new HashMap<>();
            Map<String, Enrollment> enrollmentMap = new HashMap<>();

            CsvReader csvReader = new CsvReader("/Users/Biao/Desktop/20191017.csv", ',', StandardCharsets.UTF_8);
            csvReader.readHeaders();

            int line = 0;
            long start = System.currentTimeMillis();
            while (csvReader.readRecord()) {
                // 读取一行数据
                String examineeUid  = csvReader.get(0).trim();
                String examineeName = csvReader.get(1).trim();
                String idCardNo     = csvReader.get(2).trim();
                String subjectCode  = csvReader.get(3).trim();
                String subjectName  = csvReader.get(4).trim();
                String siteCode     = csvReader.get(7).trim();
                String siteName     = csvReader.get(8).trim();
                String roomCode     = csvReader.get(9).trim();
                String seatCode     = csvReader.get(10).trim();
                String unit         = csvReader.get(11).trim();
                String startStr     = csvReader.get(12).trim() + " " + csvReader.get(13).trim();
                String endStr       = csvReader.get(14).trim() + " " + csvReader.get(15).trim();

                // [1] 报名数据放入筐中
                if (!examineeMap.containsKey(examineeUid)) {
                    // 创建密码
                    String encodedPassword = "111111";
                    if (idCardNo.length() == 18) {
                        encodedPassword = idCardNo.substring(6, 14);
                    } else if (idCardNo.length() == 15) {
                        encodedPassword = "----" + idCardNo.substring(6, 12);
                        // encodedPassword = ServiceConstants.ID_CARD_PREFIX + idCardNo.substring(6, 12);
                    }
                    encodedPassword = passwordByBCrypt(encodedPassword);

                    // 组装考生，放入暂存筐
                    Examinee examinee = new Examinee(examineeUid, examineeName, encodedPassword, idCardNo);
                    examineeMap.put(examineeUid, examinee);
                }

                if (!subjectMap.containsKey(subjectCode)) {
                    Subject subject = new Subject("userName", subjectCode, subjectName, "examCode");
                    subjectMap.put(subjectCode, subject);
                }

                if (!unitMap.containsKey(unit)) {
                    ExamUnit examUnit = new ExamUnit("userName", "examCode", unit);
                    Date startTime = Utils.stringToDate(startStr);
                    Date endTime = Utils.stringToDate(endStr);
                    examUnit.setStartTime(startTime);
                    examUnit.setEndTime(endTime);
                    unitMap.put(unit, examUnit);
                }

                if (!siteMap.containsKey(siteCode)) {
                    ExamSite examSite = new ExamSite("userName", "examCode", siteCode, siteName, siteCode);
                    siteMap.put(siteCode, examSite);
                }

                if (!roomMap.containsKey(siteCode + roomCode)) {
                    ExamRoom examRoom = new ExamRoom(siteCode, roomCode);
                    roomMap.put(siteCode + roomCode, examRoom);
                }

                String enrollmentIndex = examineeUid + subjectCode + unit + siteCode;
                if (!enrollmentMap.containsKey(enrollmentIndex)) {
                    // 考试逻辑ID：机考可以提前确定，但点考不行，三个阶段需要三个 ID，需要后面考试前临时生成；
                    String examLogicId = "";
                    // if (ServiceConstants.EXAM_TYPE_JIKAO.equalsIgnoreCase(examType)) {
                    //     examLogicId = String.format("%s-%s-%s-%s", examCode, subjectCode, unit, ServiceConstants.EXAM_PHASE_CLOSE_NUM);
                    // }else {
                        examLogicId = String.format("%s-%s-%s", "examCode", subjectCode, 1234);
                    // }

                    Enrollment enrollment = new Enrollment(examineeUid, subjectCode, siteCode, roomCode, seatCode, unit, idCardNo, "examType", "examCode", examLogicId);
                    enrollmentMap.put(enrollmentIndex, enrollment);
                }

                // 往 redis 中记录进度信息
                // System.out.println(++line);
                ++line;
            }

            long end = System.currentTimeMillis();
            System.out.println((end - start) + "ms");
        } catch (Exception ignored) {

        }
    }
}
