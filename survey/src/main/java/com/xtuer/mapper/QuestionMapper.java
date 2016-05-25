package com.xtuer.mapper;

import com.xtuer.bean.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionMapper {
    List<Question> selectQuestionsByTopicId(int topicId);

    Question selectQuestion(int id);
    List<Integer> selectQuestionIdsByTopicId(int topicId);
    void insertQuestion(Question question);
    void updateQuestion(Question question);
    void deleteQuestion(int id);
    void deleteQuestionsByTopicId(int topicId);
    void updateOrder(@Param("questionId") int questionId, @Param("order") double order);
}
