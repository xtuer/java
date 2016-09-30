package com.ebag.bean.realexam;

/**
 * 一科考试的成绩，对应数据库中的表 exam_score 的一行
 */
public class ExamScore {
    private Integer examId;       // 考试的 id，用于批量删除
    private String examName;      // 考试的名称，例如期中考试
    private String studentNumber; // 学生的学号
    private String studentName;   // 学生的名字
    private String subjectName;   // 科目的名字
    private int subjectScore;     // 科目的成绩
    private int subjectRank;      // 科目的排名

    public ExamScore() {
    }

    public ExamScore(String examName, String studentNumber, String studentName, String subjectName, int subjectScore, int subjectRank) {
        this.examName = examName;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.subjectScore = subjectScore;
        this.subjectRank = subjectRank;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getSubjectScore() {
        return subjectScore;
    }

    public void setSubjectScore(int subjectScore) {
        this.subjectScore = subjectScore;
    }

    public int getSubjectRank() {
        return subjectRank;
    }

    public void setSubjectRank(int subjectRank) {
        this.subjectRank = subjectRank;
    }

    @Override
    public String toString() {
        return "ExamScore{" +
                "examId=" + examId +
                ", examName='" + examName + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", studentName='" + studentName + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", subjectScore=" + subjectScore +
                ", subjectRank=" + subjectRank +
                '}';
    }
}
