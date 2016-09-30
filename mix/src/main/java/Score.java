import com.github.sd4324530.fastexcel.annotation.MapperCell;

public class Score {
    @MapperCell(cellName = "学号")
    private String studentNumber;

    @MapperCell(cellName = "姓名")
    private String studentName;

    public Score() {
    }

    public Score(String studentNumber, String studentName) {
        this.studentNumber = studentNumber;
        this.studentName = studentName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
