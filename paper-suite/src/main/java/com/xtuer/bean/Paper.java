package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Paper {
    private String paperId;
    private String name;
    private String uuidName;
    private String originalName;
    private String paperDirectoryId;
    private String realDirectoryName;
    private String subject;
    private String publishYear;
    private boolean exportable;

    private List<KnowledgePoint> knowledgePoints; // 知识点
}
