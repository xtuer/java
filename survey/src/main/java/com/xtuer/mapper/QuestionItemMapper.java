package com.xtuer.mapper;

import com.xtuer.bean.QuestionItem;

import java.util.List;

public interface QuestionItemMapper {
    void insert(QuestionItem item);
    void deleteQuestionItemsByQuestionId(int questionId);
    void deleteQuestionItemsByQuestionIds(List<Integer> questionIds);
}
