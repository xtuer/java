package com.xtuer.mapper;

import com.xtuer.bean.Question;
import com.xtuer.bean.QuestionKnowledgePoint;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface QuestionMapper {
    /**
     * [0] 取得所有的知识点
     *
     * @return 知识点的列表
     */
    List<QuestionKnowledgePoint> findAllQuestionKnowledgePoints();

    /**
     * [0] 查找子知识点
     *
     * @param parentId 父知识点的 ID
     * @return 知识点的列表
     */
    List<QuestionKnowledgePoint> findQuestionKnowledgePointsByParentId(long parentId);

    /**
     * [0] 查找科目下的所有知识点
     *
     * @param subjectCode 知识点的科目编码，例如 GYYK034C
     * @return 知识点的列表
     */
    List<QuestionKnowledgePoint> findQuestionKnowledgePointsBySubjectCode(String subjectCode);

    /**
     * [A] 查找知识点下的题目数量
     *
     * @param knowledgePointId 知识点的 ID
     * @return 题目的数量
     */
    int questionsCountByQuestionKnowledgePointId(long knowledgePointId);

    /**
     * [A] 查找知识点下的题目
     *
     * @param knowledgePointId 知识点的 ID
     * @param offset 分页的起始位置
     * @param size 分页的条数
     * @return 题目的列表
     */
    List<Question> findQuestionsByQuestionKnowledgePointId(@Param("knowledgePointId") long knowledgePointId,
                                                           @Param("offset") int offset,
                                                           @Param("size") int size);

    /**
     * [B] 查找科目下没有知识点的题目数量
     *
     * @param subjectCode 科目编码
     * @return 题目的数量
     */
    int noKnowledgePointQuestionsCountBySubjectCode(String subjectCode);

    /**
     * [B] 查找科目下没有知识点的题目
     *
     * @param subjectCode 科目编码
     * @param offset 分页的起始位置
     * @param size 分页的条数
     * @return 题目的列表
     */
    List<Question> findNoKnowledgePointQuestionsBySubjectCode(@Param("subjectCode") String subjectCode,
                                                              @Param("offset") int offset,
                                                              @Param("size") int size);


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
     * 清除知识点下的题目数量: 全设置为 0
     */
    void cleanQuestionCount();

    /**
     * 更新知识点下的题目数量
     */
    void updateQuestionCount();
}
