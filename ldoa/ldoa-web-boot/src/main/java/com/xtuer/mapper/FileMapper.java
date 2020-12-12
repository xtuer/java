package com.xtuer.mapper;

import com.xtuer.bean.DiskFile;
import com.xtuer.bean.Page;
import com.xtuer.bean.UploadedFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 上传文件的 Mapper
 */
@Mapper
public interface FileMapper {
    /**
     * 使用 ID 查询上传的文件
     *
     * @param id 文件 ID
     * @return 返回查找到的文件
     */
    UploadedFile findUploadedFileById(long id);

    /**
     * 插入或者更新上传的文件
     *
     * @param file 文件
     */
    void upsertUploadedFile(UploadedFile file);

    /**
     * 更新文件的 URL
     *
     * @param id   文件 ID
     * @param url  文件 URL
     * @param type 类型
     */
    void updateUploadedFileUrlAndType(long id, String url, int type);

    /**
     * 删除上传文件的记录
     *
     * @param id 文件 ID
     */
    void deleteUploadedFileById(long id);

    /**
     * 查询指定 ID 的网盘文件
     *
     * @param fileId 文件 ID
     * @return 返回查询到的文件对象，查询不到返回 null
     */
    DiskFile findDiskFileById(long fileId);

    /**
     * 查询用户 的文件，如果 userId 为 0，则查询所有用户的文件，对 filename 使用模糊匹配
     *
     * @param userId   用户 ID
     * @param filename 文件名
     * @param page     分页对象
     * @return 返回文件的数组
     */
    List<DiskFile> findDiskFiles(long userId, String filename, Page page);

    /**
     * 把文件插入到网盘
     *
     * @param fileId 文件 ID
     */
    void insertFileToDisk(long fileId);

    /**
     * 删除网盘中的文件
     *
     * @param fileId 文件 ID
     */
    void deleteDiskFile(long fileId);
}
