package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PaperDirectory {
    private Long paperDirectoryId;

    @NotBlank(message="目录名不能为空")
    private String name;
    private String uuidName;
    @NotNull(message="父目录 ID 不能为空")
    private Long parentPaperDirectoryId;
    private boolean deleted;
}
