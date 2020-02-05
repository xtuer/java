package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * 知识点
 */
@Getter
@Setter
@Accessors(chain = true)
public class KnowledgePoint {
    private String code;        // 知识点编码
    private String title;       // 知识点名称
    private String subjectCode; // 学科编码
    private Long   questionId;  // 题目 ID，题目知识点关系表 exam_question_knowledge_point 中使用
    private boolean sub;        // true 为小题的知识点，false 为普通题、大题的知识点 (方便统计非小题的数量)

    public KnowledgePoint() {
    }

    public KnowledgePoint(String code, String title, String subjectCode) {
        this.code = code;
        this.title = title;
        this.subjectCode = subjectCode;
    }

    // 提示: 2 个知识点相等，只需要 subjectCode + code 相等
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgePoint that = (KnowledgePoint) o;
        return Objects.equals(code, that.code) && Objects.equals(subjectCode, that.subjectCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, subjectCode);
    }
}
