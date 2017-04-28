package com.xtuer.mapper;

import com.xtuer.bean.KnowledgePoint;

import java.util.List;

public interface KnowledgePointMapper {
    List<KnowledgePoint> findKnowledgePointsByKnowledgePointGroupId(long knowledgePointGroupId);

    // 创建 KnowledgePoint
    void createKnowledgePoint(KnowledgePoint knowledgePoint);

    // 更新 KnowledgePoint
    void updateKnowledgePoint(KnowledgePoint knowledgePoint);

    // 标记 KnowledgePoint 为已删除
    void markKnowledgePointAsDeleted(long knowledgePointId);
}
