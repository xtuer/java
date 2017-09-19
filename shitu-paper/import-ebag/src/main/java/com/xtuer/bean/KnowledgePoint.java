package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnowledgePoint {
    private String knowledgePointId;
    private String parentKnowledgePointId;
    private String name;
    private int type; // 1 为知识点, 0 为知识点分类
    private String paperId; // 试卷 id，使用试卷查找知识点时用到
    private String tenantCode;
}
