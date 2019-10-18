package exam;

/**
 * Created by Administrator on 2016/1/6.
 */

public class Examinee {

    private int examineeId;
    private String examUId;
    private String examineeName;
    private String password;
    private String idCardNo;

    public Examinee() {
    }

    public Examinee(String examUId, String examineeName, String password, String idCardNo) {
        this.examUId = examUId;
        this.examineeName = examineeName;
        this.password = password;
        this.idCardNo = idCardNo;
    }

    public String getExamUId() {
        return examUId;
    }

    public void setExamUId(String examUId) {
        this.examUId = examUId;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public int getExamineeId() {
        return examineeId;
    }

    public void setExamineeId(int examineeId) {
        this.examineeId = examineeId;
    }

    public String getExamineeName() {
        return examineeName;
    }

    public void setExamineeName(String examineeName) {
        this.examineeName = examineeName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

