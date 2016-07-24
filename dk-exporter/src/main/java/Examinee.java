import java.util.HashMap;
import java.util.Map;

public class Examinee {
    String uid;  // 学籍号
    String name; // 学生名字
    Map<String, Exam> exams = new HashMap<String, Exam>(); // 考试信息, key 是 courseCode

    public Examinee(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Examinee{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", exams=" + exams +
                '}';
    }
}
