package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class KnowledgePoint {
    private String knowledgePointId;

    @NotBlank(message="知识点分类不能为空")
    private String parentKnowledgePointId;

    @NotBlank(message="名字不能为空")
    private String name;

    private int type; // 1 为知识点, 0 为知识点分类

    private String paperId; // 试卷 id，使用试卷查找知识点时用到
}
