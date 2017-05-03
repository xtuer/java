package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class PaperDirectory {
    private String paperDirectoryId;

    @NotBlank(message="目录名不能为空")
    private String name;
    private String uuidName;
    @NotBlank(message="父目录 ID 不能为空")
    private String parentPaperDirectoryId;
}
