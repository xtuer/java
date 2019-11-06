package com.exam.mapper;

import com.exam.bean.exam.Paper;
import com.exam.bean.exam.Question;

/**
 * 试卷的 Mapper
 */
public interface PaperMapper {
    /**
     * 插入或者更新试卷
     *
     * @param paper 试卷
     */
    void upsertPaper(Paper paper);

    /**
     * 插入或者更新试卷的题目到试卷题目表
     *
     * @param question 题目
     */
    void upsertPaperQuestion(Question question);

    /**
     * 试卷题目表中删除题目，同时删除题目的小题
     *
     * @param questionId 题目 ID
     */
    void deletePaperQuestion(long questionId);
}
