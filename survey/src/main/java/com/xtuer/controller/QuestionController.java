package com.xtuer.controller;

import com.xtuer.bean.Question;
import com.xtuer.bean.Result;
import com.xtuer.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class QuestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionController.class.getName());

    @Autowired
    private QuestionService questionService;

    /**
     * 访问管理 topic 的 questions 的页面
     * @return
     */
    @GetMapping(UriViewConstants.URI_ADMIN_QUESTIONS)
    public String adminQuestionsPage() {
        return UriViewConstants.VIEW_ADMIN_QUESTIONS;
    }

    @GetMapping(UriViewConstants.REST_QUESTIONS)
    @ResponseBody
    public List<Question> selectQuestionsByTopicId(@PathVariable int topicId) {
        return questionService.selectQuestionsByTopicId(topicId);
    }

    @GetMapping(UriViewConstants.REST_QUESTIONS_WITH_ID)
    @ResponseBody
    public Question selectQuestion(@PathVariable int questionId) {
        return questionService.selectQuestion(questionId);
    }

    @PostMapping(UriViewConstants.REST_QUESTIONS)
    @ResponseBody
    public Result insertQuestion(@RequestBody Question question) {
        return questionService.insertQuestion(question);
    }

    @PutMapping(UriViewConstants.REST_QUESTIONS_WITH_ID)
    @ResponseBody
    public Result updateQuestion(@RequestBody Question question) {
        return questionService.updateQuestion(question);
    }

    @DeleteMapping(UriViewConstants.REST_QUESTIONS_WITH_ID)
    @ResponseBody
    public Result deleteQuestion(@PathVariable int questionId) {
        return questionService.deleteQuestion(questionId);
    }

    @PutMapping(UriViewConstants.REST_QUESTIONS_ORDERS)
    @ResponseBody
    public Result updateQuestionOrders(@RequestBody List<Map<String, Integer>> map) {
        return questionService.updateQuestionOrders(map);
    }

    @GetMapping(value = UriViewConstants.REST_QUESTIONS_ANSWERS_SUGGESTIONS)
    @ResponseBody
    public List<String> selectQuestionAnswerInputs(@PathVariable int questionId,
                                                   @PathVariable int questionItemId,
                                                   @RequestParam(name="offset", defaultValue = "0", required = false) int offset) {
        return questionService.selectQuestionAnswerInputs(questionId, questionItemId, offset);
    }
}
