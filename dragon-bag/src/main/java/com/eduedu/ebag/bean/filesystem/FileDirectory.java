package com.eduedu.ebag.bean.filesystem;

/**
 * 类 FileDirectory 用来记录用户的文件, 文件夹之间的关系, 组成一颗树, 构成一个简易的文件系统.
 *
 * 数据库列名:
 * file_directory_id | display_name | file_unique_name | is_file | parent_directory_id | user_id | mark_as_deleted
 */
public class FileDirectory {
    private static final int ROOT_FILE_TREE_NODE_ID = 0; // 根目录的 id 为 0

    private int     fileDirectoryId;   // 节点的 id
    private String  displayName;       // 显示时使用的名字
    private String  fileUniqueName;    // 文件的唯一性名字, 通常使用 uuid, 文件夹不需要 fileUniqueName
    private boolean isFile;            // 如果是文件则为 true, 是文件夹为 false
    private int     parentDirectoryId; // 节点的父节点的 id
    private int     userId;            // 文件所属用户的 id
    private boolean external;          // 是否外部的文件 URL, 接入第三方资源
    private boolean markAsDeleted;     // 如果文件被标记为删除则为 true, 否则为 false

    public FileDirectory() {
    }

    /**
     * 在用户的根目录下创建文件或者文件夹, fileDirectoryId 为 0, 非第三方资源
     */
    public FileDirectory(String displayName, String fileUniqueName, boolean isFile, int userId) {
        this(0, displayName, fileUniqueName, isFile, ROOT_FILE_TREE_NODE_ID, userId, false);
    }

    /**
     * 在用户的指定目录下创建文件或者文件夹, fileDirectoryId 为 0, 非第三方资源
     */
    public FileDirectory(String displayName, String fileUniqueName, boolean isFile, int parentDirectoryId, int userId) {
        this(0, displayName, fileUniqueName, isFile, parentDirectoryId, userId, false);
    }

    /**
     * 在用户的指定目录下创建文件或者文件夹, fileDirectoryId 为 0
     */
    public FileDirectory(String displayName, String fileUniqueName, boolean isFile, int parentDirectoryId, int userId, boolean external) {
        this(0, displayName, fileUniqueName, isFile, parentDirectoryId, userId, external);
    }

    /**
     * 创建文件或者文件夹
     */
    public FileDirectory(int fileDirectoryId, String displayName, String fileUniqueName, boolean isFile,
                         int parentDirectoryId, int userId, boolean external) {
        this.fileDirectoryId = fileDirectoryId;
        this.displayName = displayName;
        this.fileUniqueName = fileUniqueName;
        this.isFile = isFile;
        this.parentDirectoryId = parentDirectoryId;
        this.userId = userId;
        this.external = external;
    }

    public int getFileDirectoryId() {
        return fileDirectoryId;
    }

    public void setFileDirectoryId(int fileDirectoryId) {
        this.fileDirectoryId = fileDirectoryId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFileUniqueName() {
        return fileUniqueName;
    }

    public void setFileUniqueName(String fileUniqueName) {
        this.fileUniqueName = fileUniqueName;
    }

    public boolean getIsFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getParentDirectoryId() {
        return parentDirectoryId;
    }

    public void setParentDirectoryId(int parentDirectoryId) {
        this.parentDirectoryId = parentDirectoryId;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public boolean isMarkAsDeleted() {
        return markAsDeleted;
    }

    public void setMarkAsDeleted(boolean markAsDeleted) {
        this.markAsDeleted = markAsDeleted;
    }
}
