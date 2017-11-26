package com.xtuer.bean;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 1 个 Topic 有多个 Question, 1 个 Question 有多个 QuestionItem
 */
@Getter
@Setter
@Accessors(chain = true)
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

    public void setUrl(String url) {
        this.url = StringUtils.trim(url);
    }
}
