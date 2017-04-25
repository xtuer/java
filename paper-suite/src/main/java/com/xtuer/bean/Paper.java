package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Paper {
    private Long   paperId;
    private String name;
    private String uuidName;
    private String originalName;
    private Long   directoryId;
    private String realDirectoryName;
    private String subject;
    private Date   year;
    private boolean deleted;
}
