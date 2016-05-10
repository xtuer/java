package com.eduedu.ebag.bean.filesystem;

import java.util.Date;

// 数据库列名
// file_node_id | original_name | unique_name | directory | md5 | upload_datetime
public class FileInfo {
    private int    fileNodeId;     // 文件的 id
    private String originalName;   // 上传的文件的原始名字
    private String uniqueName;     // 文件的唯一性名字, 通常使用 uuid
    private String directory;      // 文件在系统中的目录(相对于指定的 base 目录)
    private String md5;            // 文件的 MD5
    private long   size;           // 文件的大小, 单位为 Byte
    private Date   uploadDateTime; // 上传的时间

    public FileInfo() {
    }

    /**
     * 创建一个新的文件
     */
    public FileInfo(String originalName, String uniqueName, String directory, long size, String md5, Date uploadDateTime) {
        this.originalName   = originalName;
        this.uniqueName     = uniqueName;
        this.directory      = directory;
        this.size           = size;
        this.md5            = md5;
        this.uploadDateTime = uploadDateTime;
    }

    public int getFileNodeId() {
        return fileNodeId;
    }

    public void setFileNodeId(int fileNodeId) {
        this.fileNodeId = fileNodeId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Date getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(Date uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }
}
