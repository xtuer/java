package com.xtuer.mapper;

import com.xtuer.bean.KnowledgePoint;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KnowledgePointMapper {
    // 查找指定知识点下的知识点
    List<KnowledgePoint> findKnowledgePoints(@Param("parentKnowledgePointId") String parentKnowledgePointId, @Param("type") int type);

    // 查找所有知识点分类
    List<KnowledgePoint> findAllKnowledgePointGroups();

    // 查找分类下的知识点
    List<KnowledgePoint> findKnowledgePointsInGroup(String knowledgePointGroupId);

    // 分类下是否有知识点
    boolean hasKnowledgePoints(String knowledgePointId);

    // 知识点下是否有试卷
    boolean hasPapers(String knowledgePointId);

    // 创建知识点
    void createKnowledgePoint(KnowledgePoint knowledgePoint);

    // 更新 KnowledgePoint
    void updateKnowledgePoint(KnowledgePoint knowledgePoint);

    // 移动知识点到其他分类
    void reparentKnowledgePoint(@Param("knowledgePointId") String knowledgePointId,
                                @Param("newParentKnowledgePointId") String newParentKnowledgePointId);

    // 重命名知识点
    void renameKnowledgePoint(@Param("knowledgePointId") String knowledgePointId, @Param("name") String name);

    // 删除 KnowledgePoint
    void deleteKnowledgePoint(String knowledgePointId);
}
