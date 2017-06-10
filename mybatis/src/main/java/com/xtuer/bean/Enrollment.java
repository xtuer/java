package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
