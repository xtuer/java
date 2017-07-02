package com.xtuer.mapper;

import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.PaperDirectory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PaperDirectoryMapper {
    // 获取所有的目录
    List<PaperDirectory> getAllPaperDirectories();

    // 查找父目录下的目录
    List<PaperDirectory> findPaperSubdirectories(@Param("paperDirectoryId") String paperDirectoryId);

    // 创建目录
    void createPaperDirectory(PaperDirectory paperDirectory);

    // 修改目录的父目录 id
    void changeParentPaperDirectoryId(@Param("paperDirectoryId") String paperDirectoryId,
                                      @Param("parentPaperDirectoryId") String parentPaperDirectoryId);

    // 重命名目录
    void renamePaperDirectory(@Param("paperDirectoryId") String paperDirectoryId, @Param("name") String name);

    // 是否有子目录
    boolean hasPaperSubdirectories(String parentPaperDirectoryId);

    // 目录 paperDirectoryId 中是否有文件
    boolean hasPapers(String paperDirectoryId);

    boolean isPaperDirectoryExisting(String paperDirectoryId);

    // 设置 is_deleted 为 1，标记目录已经被删除了
    void markPaperDirectoryAsDeleted(String paperDirectoryId);

    // 查询所有目录下试卷的数量
    List<Map<String, String>> findPaperCountsInPaperDirectories();

    // 查询目录下试卷的所有知识点
    List<KnowledgePoint> findKnowledgePointsInPaperDirectory(String paperDirectoryId);
}
