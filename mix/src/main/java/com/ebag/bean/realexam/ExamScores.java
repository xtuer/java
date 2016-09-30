package com.ebag.bean.realexam;

import java.util.HashSet;
import java.util.Set;

/**
 * 使用树的层次结构存储同一个学生的所有考试成绩，便于转换为 JSON
 *
 * 结构如下:
 * ExamScores
 *     学生信息
 *     科目
 *         单元测试一成绩
 *         单元测试二成绩
 *         期中考试成绩
 *         单元测试三成绩
 *         期末考试成绩
 */
public class ExamScores {
    private String studentNumber;
    private String studentName;
    private int studentCount;
    private Set<Subject> subjects = new HashSet<Subject>(); // 科目，例如数学、语文、英语等

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

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }
}
