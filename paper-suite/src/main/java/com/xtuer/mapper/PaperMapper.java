package com.xtuer.mapper;

import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Paper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PaperMapper {
    // 查找试卷
    List<Paper> findPaperByPaperId(String paperId);

    // 目录下试卷的数量
    int papersCountByPaperDirectoryId(String paperDirectoryId);

    // 查找目录下的试卷
    List<Paper> findPapersByPaperDirectoryId(@Param("paperDirectoryId") String paperDirectoryId,
                                             @Param("offset") int offset,
                                             @Param("count") int count);

    // 查找试卷的知识点
    List<KnowledgePoint> findKnowledgePointByPaperId(String paperId);



    // 根据学科和名字查找前 50 个试卷
    List<Paper> findPapersBySubjectAndNameFilterNotInPaperDirectory(@Param("subject") String subject, @Param("nameFilter") String nameFilter);
}
