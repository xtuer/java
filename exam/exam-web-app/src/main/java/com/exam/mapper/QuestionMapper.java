package com.exam.mapper;

import com.exam.bean.exam.Question;
import com.exam.bean.exam.QuestionOption;

/**
 * 题目的 Mapper
 */
public interface QuestionMapper {
    /**
     * 插入或者更新题目
     *
     * @param question 题目
     */
    void insertOrUpdateQuestion(Question question);

    /**
     * 插入或者更新题目的选项
     *
     * @param option 选项
     */
    void insertOrUpdateQuestionOption(QuestionOption option);

    /**
     * 删除题目
     * 同时会删除题目的选项，题目的小题以及小题的选项
     *
     * @param questionId 题目 ID
     */
    void deleteQuestion(long questionId);

    /**
     * 删除选项
     *
     * @param optionId 选项 ID
     */
    void deleteQuestionOption(long optionId);
}
