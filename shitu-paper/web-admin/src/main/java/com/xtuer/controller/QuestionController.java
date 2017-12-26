package com.xtuer.controller;

import com.xtuer.bean.Question;
import com.xtuer.bean.QuestionKnowledgePoint;
import com.xtuer.bean.Result;
import com.xtuer.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 查找知识点
     * URL: http://localhost:8080/rest/questionKnowledgePoints/128344304878878720/children
     *
     * @param parentId 父知识点的 ID
     */
    @GetMapping(UriView.REST_QUESTION_KNOWLEDGE_POINTS_BY_PARENT_ID)
    @ResponseBody
    public Result<List<QuestionKnowledgePoint>> findQuestionKnowledgePointsByParentId(@PathVariable Long parentId) {
        return Result.ok("success", questionMapper.findQuestionKnowledgePointsByParentId(parentId));
    }

    /**
     * 查找知识点下的单题
     * URL: http://localhost:8080/rest/questionKnowledgePoints/128344304602054663/questions
     *
     * @param questionKnowledgePointId 知识点的 ID
     */
    @GetMapping(UriView.REST_QUESTIONS_UNDER_KNOWLEDGE_POINT)
    @ResponseBody
    public Result<List<Question>> findQuestionUnderQuestionKnowledgePoint(@PathVariable Long questionKnowledgePointId) {
        return Result.ok("success", questionMapper.findQuestionsUnderQuestionKnowledgePoint(questionKnowledgePointId));
    }

    /**
     * 标记题目
     * URL: http://localhost:8080/rest/questions/129751762465718272/mark
     *
     * @param questionId 题目的 ID
     */
    @PutMapping(UriView.REST_MARK_QUESTION)
    @ResponseBody
    public Result markQuestion(@PathVariable Long questionId) {
        questionMapper.markQuestion(questionId);
        return Result.ok();
    }

    /**
     * 查找所有被标记过的题目的原始 ID
     * URL: http://localhost:8080/rest/questionIds/marked
     */
    @GetMapping(UriView.REST_MARKED_QUESTION_IDS)
    @ResponseBody
    public Result<List<String>> findMarkedQuestionOriginalIds() {
        return Result.ok("success", questionMapper.findMarkedQuestionOriginalIds());
    }
}
