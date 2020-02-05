package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * 章节知识点关系: 一个章节可能有多个相关的知识点
 */
@Getter
@Setter
@Accessors(chain = true)
public final class ChapterKnowledgePoint {
    private String bookCode;
    private String chapterCode;
    private String chapterComment;
    private Map<String, String> knowledgePoints = new HashMap<>(); // 知识点: key(编码), value(名字)
}
