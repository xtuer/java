package com.xtuer.bean;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * 1 个 Topic 有多个 Question, 1 个 Question 有多个 QuestionItem
 */
public class Topic {
    private int id;
    private String content;
    private String url;
    private List<Question> questions;
    private boolean forceComplete;

    public Topic() {
    }

    public Topic(int id, String content, String url, boolean forceComplete) {
        this.id = id;
        this.content = content;
        this.setUrl(url);
        this.forceComplete = forceComplete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = StringUtils.trim(url);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public boolean isForceComplete() {
        return forceComplete;
    }

    public void setForceComplete(boolean forceComplete) {
        this.forceComplete = forceComplete;
    }
}
