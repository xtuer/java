package exam;

import java.util.Date;

/**
 * Created by Administrator on 2016/1/8.
 */

public class ExamUnit implements Comparable<ExamUnit> {
    private int unitId;
    private String userName;
    private String examCode;
    private String unit;
    private Date startTime;
    private Date endTime;
    private Date openEnd;
    private boolean started;
    private boolean ended;

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public ExamUnit() {
    }

    public ExamUnit(String userName, String examCode, String unit) {
        this.userName = userName;
        this.examCode = examCode;
        this.unit = unit;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getExamCode() {
        return examCode;
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getOpenEnd() {
        return openEnd;
    }

    public void setOpenEnd(Date openEnd) {
        this.openEnd = openEnd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // public SignInExamUnit toSignInExamUnit() {
    //     return new SignInExamUnit(this.getExamCode(), this.getUnit(), this.getStartTime(), this.getEndTime());
    // }

    @Override
    public int compareTo(ExamUnit o) {
        if(null == o) {
            return -1;
        }

        long hostTime = this.getStartTime().getTime();
        long guestTime = o.getStartTime().getTime();

        if (hostTime < guestTime) {
            return -1;
        } else if (hostTime == guestTime) {
            return 0;
        } else {
            return 1;
        }
    }
}
