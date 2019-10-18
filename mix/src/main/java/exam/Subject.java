package exam;

/**
 * Created by Administrator on 2016/1/8.
 */

public class Subject {

    private int subjectId;
    private String userName;
    private String subjectCode;
    private String subjectName;
    private String examCode;

    public Subject() {}

    public Subject(String subjectCode, String examCode) {
        this.subjectCode = subjectCode;
        this.examCode = examCode;
    }

    public Subject(String userName, String examCode, String subjectCode, String subjectName) {
        this.userName = userName;
        this.examCode = examCode;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getExamCode() {
        return examCode;
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
