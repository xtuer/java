package com.ebag.bean.realexam;

import java.util.LinkedList;
import java.util.List;

/**
 * 一个科目，可以有多次考试成绩
 */
public class Subject {
    private String name; // 科目名字，例如数学、语文
    private List<ExamScore> scores = new LinkedList<ExamScore>(); // 同一个科目的多次成绩

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExamScore> getScores() {
        return scores;
    }

    public void setScores(List<ExamScore> scores) {
        this.scores = scores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        return name != null ? name.equals(subject.name) : subject.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
