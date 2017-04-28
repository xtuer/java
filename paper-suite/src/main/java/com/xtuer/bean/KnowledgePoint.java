package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class KnowledgePoint {
    private Long knowledgePointId;

    @NotBlank(message="知识点不能为空")
    private String name;

    @NotNull(message="知识点分类不能为 null")
    private Long knowledgePointGroupId;
}
