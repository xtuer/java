package com.exam.mapper.exam;

import com.exam.bean.Page;
import com.exam.bean.exam.Paper;
import com.exam.bean.exam.Question;

import java.util.List;

/**
 * 试卷的 Mapper
 */
public interface PaperMapper {
    /**
     * 查找指定 ID 的试卷
     * [注意]: 不要直接使用这个方法，应该使用 PaperService.findPaperById(paperId);
     *
     * @param paperId 试卷 ID
     * @return 返回查找到的试卷，查不到返回 null
     */
    Paper findPaperById(long paperId);

    /**
     * 查询指定机构 ID 的试卷
     *
     * @param orgId 机构 ID
     * @param title 试卷标题，可模糊搜索
     * @param page  分页对象
     * @return 返回机构的试卷数组
     */
    List<Paper> findPapersByOrgId(long orgId, String title, Page page);

    /**
     * 判断 ID 为传入的 paperId 的试卷是否存在
     *
     * @param paperId 试卷 ID
     * @return 试卷存在返回 true，不存在返回 false
     */
    boolean paperExists(long paperId);

    /**
     * 判断试卷是否客观题试卷
     *
     * @param paperId 试卷 ID
     * @return 全是客观题的试卷返回 true，否则返回 false
     */
    boolean isObjectivePaper(long paperId);

    /**
     * 插入或者更新试卷
     *
     * @param paper 试卷
     */
    void upsertPaper(Paper paper);

    /**
     * 插入或者更新试卷的题目到试卷题目表 (不会更新小题的，因为逻辑复杂)
     *
     * @param question 题目
     */
    void upsertPaperQuestion(Question question);

    /**
     * 试卷题目表中删除题目，同时删除题目表中题目的小题
     *
     * @param questionId 题目 ID
     */
    void deletePaperQuestion(long questionId);

    /**
     * 查找试卷的题目
     * [注意]: 不要直接使用这个方法，应该使用 QuestionService.findPaperQuestions(paperId)
     *
     * @param paperId 试卷 ID
     * @return 返回题目的数组
     */
    List<Question> findPaperQuestionsByPaperId(long paperId);
}
