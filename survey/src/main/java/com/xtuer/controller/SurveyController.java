package com.xtuer.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtuer.bean.Answer;
import com.xtuer.bean.Question;
import com.xtuer.bean.Result;
import com.xtuer.bean.Topic;
import com.xtuer.service.QuestionService;
import com.xtuer.service.SurveyService;
import com.xtuer.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class SurveyController {
    private static Logger logger = LoggerFactory.getLogger(SurveyController.class.getName());

    @Autowired
    private TopicService topicService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SurveyService surveyService;

    /**
     * Survey 页面访问量很大, 可以先生成静态的后访问, 这里只做了简单处理.
     *
     * @param surveyId 就是 topic id
     * @param modal
     * @return
     */
    @RequestMapping(UriViewConstants.URI_SURVEYS)
    public String surveyPage(@PathVariable int surveyId, ModelMap modal) {
        Topic topic = topicService.selectTopicById(surveyId);

        // 找不到 topic 则访问 404 页面
        if (topic == null) {
            logger.info("问卷 {} 不存在", surveyId);
            return UriViewConstants.VIEW_404;
        }

        List<Question> questions = questionService.selectQuestionsByTopicId(surveyId);

        if (questions == null || questions.size() == 0) {
            logger.info("问卷 {} 没有问题", surveyId);
            return UriViewConstants.VIEW_404;
        }

        modal.put("topic", topic);
        modal.put("questions", questions);

        return UriViewConstants.VIEW_SURVEYS;
    }

    /**
     * 处理调查问卷的提交
     * @return
     */
    @RequestMapping(value = UriViewConstants.URI_SURVEYS_SUBMIT, method = RequestMethod.POST)
    public String submitSurvey(@RequestParam("answers") String answersString) throws IOException {
        logger.debug("Answers: {}", answersString);

        // 1. 空字符串也到更新成功页
        answersString = StringUtils.trimWhitespace(answersString);
        if (StringUtils.isEmpty(answersString)) {
            logger.info("答案为空");
            return UriViewConstants.REDIRECT + UriViewConstants.SUCCESS;
        }

        // 反序列化 Json 表示的 Answer list
        ObjectMapper mapper = new ObjectMapper();
        List<Answer> answers = mapper.readValue(answersString, new TypeReference<List<Answer>>(){});

        // 2. 没有答案, 只是提示保存成功
        if (answers == null || answers.size() == 0) {
            logger.info("没有答案");
            return UriViewConstants.REDIRECT + UriViewConstants.SUCCESS;
        }

        // 3. 把答案保存到数据库
        Result result = surveyService.insertAnswers(answers);

        if (!result.isSuccess()) {
            logger.info("保存答案出错: {}", result.getMessage());
            return UriViewConstants.VIEW_404;
        }

        // 4. 如果有下一步指定的页面, 则访问指定的页面, 否则访问提交成功页面
        String url = topicService.getTopicUrl(answers.get(0).getTopicId());
        logger.info("问卷提交后访问: " + url);

        if (!StringUtils.isEmpty(url)) {
            return UriViewConstants.REDIRECT + url;
        } else {
            return UriViewConstants.REDIRECT + UriViewConstants.SUBMIT_SUCCESS;
        }
    }
}
