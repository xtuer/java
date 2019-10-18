package exam;

import java.util.Date;

public class Enrollment {

    private int enrollmentId;
    private String examUId;
    private String idCardNo;
    private String subjectCode;
    private String siteCode;
    private String roomCode;
    private String seatCode;
    private String unit;
    private String examType;
    private String examCode;
    private String examLogicId;
    private String gkEvaluationPlanId;
    private Date openStart;
    private Date openEnd;
    private Date closeStart;
    private Date closeEnd;

    public String getGkEvaluationPlanId() {
        return gkEvaluationPlanId;
    }

    public void setGkEvaluationPlanId(String gkEvaluationPlanId) {
        this.gkEvaluationPlanId = gkEvaluationPlanId;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public Enrollment() {
    }

    public Enrollment(String examUId, String subjectCode) {
        this.examUId = examUId;
        this.subjectCode = subjectCode;
    }

    public Enrollment(String examUId, String subjectCode, String examCode) {
        this.examUId = examUId;
        this.subjectCode = subjectCode;
        this.examCode = examCode;
    }

    public Enrollment(String examUId, String subjectCode, String siteCode, String roomCode, String seatCode, String unit,
                      String idCardNo, String examType, String examCode, String examLogicId) {
        this.examUId = examUId;
        this.subjectCode = subjectCode;
        this.siteCode = siteCode;
        this.roomCode = roomCode;
        this.seatCode = seatCode;
        this.unit = unit;
        this.idCardNo = idCardNo;
        this.examType = examType;
        this.examCode = examCode;
        this.examLogicId = examLogicId;
    }

    public Enrollment(String examUId, String subjectCode, String siteCode, String roomCode, String seatCode, String unit,
                      String idCardNo, String examType, String examCode, String examLogicId, String gkEvaluationPlanId) {
        this.examUId = examUId;
        this.subjectCode = subjectCode;
        this.siteCode = siteCode;
        this.roomCode = roomCode;
        this.seatCode = seatCode;
        this.unit = unit;
        this.idCardNo = idCardNo;
        this.examType = examType;
        this.examCode = examCode;
        this.examLogicId = examLogicId;
        this.gkEvaluationPlanId = gkEvaluationPlanId;
    }

    public Enrollment(String examCode, String unit, String siteCode, String roomCode, String subjectCode) {
        this.examCode = examCode;
        this.subjectCode = subjectCode;
        this.siteCode = siteCode;
        this.roomCode = roomCode;
        this.unit = unit;
    }

    public void setPhaseTime(Date openStart, Date openEnd, Date closeStart, Date closeEnd) {
        this.setOpenStart(openStart);
        this.setOpenEnd(openEnd);
        this.setCloseStart(closeStart);
        this.setCloseEnd(closeEnd);
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getOpenStart() {
        return openStart;
    }

    public void setOpenStart(Date openStart) {
        this.openStart = openStart;
    }

    public Date getOpenEnd() {
        return openEnd;
    }

    public void setOpenEnd(Date openEnd) {
        this.openEnd = openEnd;
    }

    public Date getCloseStart() {
        return closeStart;
    }

    public void setCloseStart(Date closeStart) {
        this.closeStart = closeStart;
    }

    public Date getCloseEnd() {
        return closeEnd;
    }

    public void setCloseEnd(Date closeEnd) {
        this.closeEnd = closeEnd;
    }

    public String getExamCode() {
        return examCode;
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public String getExamLogicId() {
        return examLogicId;
    }

    public void setExamLogicId(String examLogicId) {
        this.examLogicId = examLogicId;
    }
}
