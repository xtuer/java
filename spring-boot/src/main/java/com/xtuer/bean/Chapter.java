package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 章节目录
 */
@Getter
@Setter
@Accessors(chain = true)
public class Chapter {
    private int id;            // 无意义的主键 ID
    private String code;       // 章节编码
    private String title;      // 章节名称
    private String bookCode;   // 教材编码
    private Long   questionId; // 题目 ID，题目章节关系表 exam_question_chapter 中使用

    private String comment;    // 章节注释 (查询章节和相关知识点时使用)
    private List<KnowledgePoint> knowledgePoints = new LinkedList<>(); // 知识点

    public Chapter() {
    }

    public Chapter(String code, String title, String bookCode) {
        this.code = code;
        this.title = title;
        this.bookCode = bookCode;
    }

    // 提示: 2 个章节相等，只需要 bookCode + code 相等
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(code, chapter.code) && Objects.equals(bookCode, chapter.bookCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, bookCode);
    }
}
