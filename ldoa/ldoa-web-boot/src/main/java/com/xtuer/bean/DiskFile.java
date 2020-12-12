package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 网盘的文件
 */
@Getter
@Setter
public class DiskFile {
    private long   fileId;      // 每个上传的文件都有一个唯一的 ID
    private String filename;    // 文件的原始名字
    private String url;         // 访问文件的 URL
    private long   userId;      // 上传文件的用户的 ID
    private String nickname;    // 上传文件的用户的名字
    private Date   createdAt;   // 创建时间
}
