package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class KnowledgePoint {
    private String knowledgePointId; // uuid

    @NotBlank(message="知识点不能为空")
    private String name;

    @NotBlank(message="知识点分类不能为 null")
    private String knowledgePointGroupId;

    private String paperId;
}
