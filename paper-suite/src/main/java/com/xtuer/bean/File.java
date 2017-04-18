package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class File {
    private Long fileId;
    private String name;
    private String uuidName;
    private String originalName;
    private Long directoryId;
    private String realDirectoryName;
    private Date year;
    private boolean deleted;
}
