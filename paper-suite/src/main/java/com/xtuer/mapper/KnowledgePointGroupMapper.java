package com.xtuer.mapper;

import com.xtuer.bean.KnowledgePointGroup;

import java.util.List;

public interface KnowledgePointGroupMapper {
    // 返回所有的 KnowledgePointGroup
    List<KnowledgePointGroup> findAllKnowledgePointGroups();

    // 创建 KnowledgePointGroup
    void createKnowledgePointGroup(KnowledgePointGroup group);

    // 更新 KnowledgePointGroup
    void updateKnowledgePointGroup(KnowledgePointGroup group);

    // 标记 KnowledgePointGroup 为已删除
    void markKnowledgePointGroupAsDeleted(String knowledgePointGroupId);
}
