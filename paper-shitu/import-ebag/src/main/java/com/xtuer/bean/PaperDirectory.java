package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaperDirectory {
    private String paperDirectoryId;
    private String name;
    private String parentPaperDirectoryId;
}
