package com.xtuer.mapper;

import com.xtuer.bean.PaperDirectory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PaperDirectoryMapper {
    // 查找父目录下的目录
    List<PaperDirectory> findPaperSubdirectories(@Param("paperDirectoryId") long paperDirectoryId);

    // 创建目录
    void createPaperDirectory(PaperDirectory paperDirectory);

    // 修改目录的父目录 id
    void changeParentPaperDirectoryId(@Param("paperDirectoryId") long paperDirectoryId, @Param("parentPaperDirectoryId") long parentPaperDirectoryId);

    // 重命名目录
    void renamePaperDirectory(@Param("paperDirectoryId") long paperDirectoryId, @Param("name") String name);

    // 是否有子目录
    boolean hasPaperSubdirectories(long parentPaperDirectoryId);

    // 目录 paperDirectoryId 中是否有文件
    boolean hasPapers(long paperDirectoryId);

    // 设置 is_deleted 为 1，标记目录已经被删除了
    void markPaperDirectoryAsDeleted(@Param("paperDirectoryId") long paperDirectoryId);
}
