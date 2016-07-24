import java.util.HashMap;
import java.util.Map;

public class Exam {
    String courseCode; // 课程编码
    String courseName; // 课程名字
    String roomCode;   // 考场代号
    String roomName;   // 考场名字
    Map<Integer, Double> scores = new HashMap<Integer, Double>(); // 每个阶段的成绩

    public Exam(String courseCode) {
        this.courseCode = courseCode;

        scores.put(1, 0.0); // 1 是练习
        scores.put(2, 0.0); // 2 是封闭
        scores.put(3, 0.0); // 3 是开放
    }

    @Override
    public String toString() {
        return "Exam{" +
                "courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", roomCode='" + roomCode + '\'' +
                ", roomName='" + roomName + '\'' +
                ", scores=" + scores +
                '}';
    }
}
