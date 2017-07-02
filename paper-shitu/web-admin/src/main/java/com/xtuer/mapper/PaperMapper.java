package com.xtuer.mapper;

import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Paper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PaperMapper {
    // 查找目录下的试卷
    List<Paper> findPapersInPaperDirectories(@Param("paperDirectoryIds") List<String> paperDirectoryIds, @Param("status") int status);

    // 查找目录下试卷用到的知识点关系
    List<KnowledgePoint> findPaperKnowledgePointsRelationInPaperDirectories(@Param("paperDirectoryIds") List<String> paperDirectoryIds,
                                                                            @Param("status") int status);

    // 查找试卷
    Paper findPaperByPaperId(String paperId);

    // 目录下试卷的数量
    int papersCountByPaperDirectoryId(String paperDirectoryId);

    // 查找目录下的试卷
    List<Paper> findPapersByPaperDirectoryId(@Param("paperDirectoryId") String paperDirectoryId,
                                             @Param("offset") int offset,
                                             @Param("size") int size);

    // 目录下试卷的数量
    int countPapersByPaperDirectoryId(String paperDirectoryId);

    // 查找试卷的知识点
    List<KnowledgePoint> findKnowledgePointsByPaperId(String paperId);

    // 查找多个试卷的知识点
    List<KnowledgePoint> findKnowledgePointsByPaperIds(@Param("paperIds") List<String> paperIds);

    // 根据学科和名字查找前 50 个试卷
    List<Paper> findPapersBySubjectAndNameFilterNotInPaperDirectory(@Param("subject") String subject, @Param("nameFilter") String nameFilter);

    // 查找目录下带知识点的试卷
    List<Paper> findPapersByPaperDirectoryIdWithKnowledgePointIds(@Param("paperDirectoryId") String paperDirectoryId,
                                                                  @Param("knowledgePointIds") List<String> knowledgePointIds,
                                                                  @Param("offset") int offset,
                                                                  @Param("size") int size);

    // 目录下带知识点的试卷的数量
    int countPapersByPaperDirectoryIdWithKnowledgePointIds(@Param("paperDirectoryId") String paperDirectoryId,
                                                           @Param("knowledgePointIds") List<String> knowledgePointIds);

    // 设置试卷的目录
    void setParentPaperDirectory(@Param("paperId") String paperId, @Param("paperDirectoryId") String paperDirectoryId);

    // 设置多个试卷的目录
    void setPapersPaperDirectory(@Param("paperDirectoryId") String paperDirectoryId, @Param("paperIds") List paperIds);

    // 查看试卷是否有知识点
    boolean hasKnowledgePoint(@Param("paperId") String paperId, @Param("knowledgePointId") String knowledgePointId);

    // 给试卷添加知识点
    void addKnowledgePoint(@Param("paperId") String paperId, @Param("knowledgePointId") String knowledgePointId);

    // 更新试卷，目前只能更新名字和发布时间
    void updatePaper(Paper paper);

    void deleteKnowledgePoint(@Param("paperId") String paperId, @Param("knowledgePointId") String knowledgePointId);
}
