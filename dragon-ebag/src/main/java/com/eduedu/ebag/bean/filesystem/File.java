package com.eduedu.ebag.bean.filesystem;

/**
 * 类 File 用来记录用户的文件, 文件夹之间的关系, 组成一颗树, 构成一个简易的文件系统.
 * 文件夹是特殊的文件, 也即是目录, 链接也是一种特殊的文件.
 *
 * 数据库列名:
 * file_id | display_name | unique_name | type | directory_id | user_id | mark_as_deleted
 */
public class File {
    public static final int ROOT_DIRECTORY_ID = 0; // 根目录的 id 为 0
    public static final int TYPE_DIRECTORY = 0;    // 目录
    public static final int TYPE_FILE = 1;         // 文件
    public static final int TYPE_LINK = 2;         // 链接

    private int     fileId;         // 文件的 id
    private String  displayName;    // 显示时使用的名字
    private String  uniqueName;     // 文件的唯一性名字, 通常使用 uuid, 文件夹不需要 uniqueName
    private int     directoryId;    // 所在目录的 id (引用的是 file_id)
    private int     type;           // 文件的类型: 0 为文件夹, 1 为普通文件, 2 为链接
    private int     userId;         // 文件所属用户的 id
    private boolean shared;         // 是否分享
    private boolean markAsDeleted;  // 如果文件被标记为删除则为 true, 否则为 false

    public File() {
    }

    /**
     * 在用户的根目录下创建文件或者文件夹, fileId 为 0
     */
    public File(String displayName, String uniqueName, int type, int userId) {
        this(displayName, uniqueName, ROOT_DIRECTORY_ID, type, userId);
    }

    /**
     * 创建文件或者文件夹
     */
    public File(String displayName, String uniqueName, int directoryId, int type, int userId) {
        this.fileId = fileId;
        this.displayName = displayName;
        this.uniqueName = uniqueName;
        this.directoryId = directoryId;
        this.type = type;
        this.userId = userId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public int getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(int directoryId) {
        this.directoryId = directoryId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isMarkAsDeleted() {
        return markAsDeleted;
    }

    public void setMarkAsDeleted(boolean markAsDeleted) {
        this.markAsDeleted = markAsDeleted;
    }

    public void setExtension(String extension) {

    }

    public String getExtension() {
        int lastDotIndex = uniqueName.lastIndexOf(".");

        if (lastDotIndex != -1) {
            return uniqueName.substring(lastDotIndex + 1);
        } else {
            return "";
        }
    }

    public boolean isFile() {
        return TYPE_FILE == type;
    }

    public boolean isDirectory() {
        return TYPE_DIRECTORY == type;
    }

    public boolean isLink() {
        return TYPE_LINK == type;
    }
}
