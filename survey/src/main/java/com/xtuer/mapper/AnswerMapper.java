package com.xtuer.mapper;

import com.xtuer.bean.Answer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AnswerMapper {
    void insertAnswer(Answer answer);
    List<Map<String, String>> topicAnswersStatistic(int topicId);
    List<String> selectQuestionAnswerInputs(@Param("questionId") int questionId,
                                            @Param("questionItemId") int questionItemId,
                                            @Param("offset") int offset);
}
