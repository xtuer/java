package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class KnowledgePointGroup {
    private String knowledgePointGroupId;

    @NotBlank(message="名字不能为空")
    private String name;
}
