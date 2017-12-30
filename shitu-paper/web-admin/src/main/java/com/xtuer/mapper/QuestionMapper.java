package com.xtuer.mapper;

import com.xtuer.bean.Question;
import com.xtuer.bean.QuestionKnowledgePoint;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface QuestionMapper {
    /**
     * 查找知识点
     *
     * @param parentId 父知识点的 ID
     * @return 知识点的列表
     */
    List<QuestionKnowledgePoint> findQuestionKnowledgePointsByParentId(long parentId);

    /**
     * 查找知识点下的单题
     *
     * @param questionKnowledgePointId 知识点的 ID
     * @return 单题的列表
     */
    List<Question> findQuestionsByQuestionKnowledgePointId(long questionKnowledgePointId);

    /**
     * 查找所有被标记过的题目的原始 ID
     *
     * @return 原始 ID 的列表
     */
    List<String> findMarkedQuestionOriginalIds();

    /**
     * Toggle 标记题目，检查题目的时候发现题目有问题，标记起来，为了后期批量导出
     *
     * @param questionId 题目的 ID
     */
    void toggleQuestionMark(@Param("questionId") long questionId);

    /**
     * 知识点相关的题目数量
     *
     * @return key 为知识点 ID，value 为此知识点下的题目数量
     */
    List<Map<Long, Integer>> questionCountOfQuestionKnowledgePoint();
}
