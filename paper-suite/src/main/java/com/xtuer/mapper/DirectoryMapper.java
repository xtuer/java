package com.xtuer.mapper;

import com.xtuer.bean.Directory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DirectoryMapper {
    // 查找父目录下的目录
    List<Directory> findDirectoriesByParentDirectoryId(@Param("parentDirectoryId") long parentDirectoryId);

    // 创建目录
    void createDirectory(Directory directory);

    // 修改目录的父目录 id
    void changeParentDirectoryId(@Param("directoryId") long directoryId, @Param("parentDirectoryId") long parentDirectoryId);

    // 重命名目录
    void renameDirectory(@Param("directoryId") long directoryId, @Param("name") String name);

    // 是否有子目录
    boolean hasSubdirectories(long directoryId);

    // 目录 directoryId 中是否有文件
    boolean hasFiles(long directoryId);

    // 设置 is_deleted 为 1，标记目录已经被删除了
    void markDirectoryAsDeleted(@Param("directoryId") long directoryId);
}
