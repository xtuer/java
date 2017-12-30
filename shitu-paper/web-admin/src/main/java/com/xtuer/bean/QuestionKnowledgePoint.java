package com.xtuer.bean;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

/**
 * 单题的知识点，和试卷的知识点结构不一样
 */
@Getter
@Setter
@Accessors(chain = true)
@JSONType(ignores = {"children"})
public class QuestionKnowledgePoint {
    private Long   id = 0L;
    private Long   parentId = 0L;
    private String code; // 知识点编码
    private String name; // 知识点名称
    private String subjectName; // 科目名称，例如物理
    private String subjectCode; // 科目编码，例如 GZWL061B

    List<QuestionKnowledgePoint> children = new LinkedList<>();

    public QuestionKnowledgePoint() {

    }

    public QuestionKnowledgePoint(String code, String name, String subjectName, String subjectCode) {
        this.code = code;
        this.name = name;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }
}
