/**
 * Created by Administrator on 2016/1/11.
 */

public class Enrollment {

    private int enrollmentId;
    private String examUId;
    private String subjectCode;
    private String siteCode;
    private String roomCode;
    private String seatCode;
    private String periodUnitCode;

    public Enrollment() {
    }

    public Enrollment(String examUId, String subjectCode) {
        this.examUId = examUId;
        this.subjectCode = subjectCode;
    }

    public Enrollment(String examUId, String subjectCode, String siteCode, String roomCode, String seatCode, String periodUnitCode) {
        this.examUId = examUId;
        this.subjectCode = subjectCode;
        this.siteCode = siteCode;
        this.roomCode = roomCode;
        this.seatCode = seatCode;
        this.periodUnitCode = periodUnitCode;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getExamUId() {
        return examUId;
    }

    public void setExamUId(String examUId) {
        this.examUId = examUId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public String getPeriodUnitCode() {
        return periodUnitCode;
    }

    public void setPeriodUnitCode(String periodUnitCode) {
        this.periodUnitCode = periodUnitCode;
    }
}
