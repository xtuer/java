package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Directory {
    private Long directoryId;

    @NotBlank(message="目录名不能为空")
    private String name;
    private String uuidName;

    @NotNull(message="父目录 ID 不能为 null")
    private Long parentDirectoryId;
    private boolean deleted;
}
