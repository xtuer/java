package com.xtuer.service;

import com.xtuer.mapper.AnswerMapper;
import com.xtuer.bean.Answer;
import com.xtuer.bean.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SurveyService {
    private static Logger logger = LoggerFactory.getLogger(SurveyService.class.getName());

    @Autowired
    private AnswerMapper answerMapper;

    @Transactional
    public Result insertAnswers(List<Answer> answers) {
        if (answers == null || answers.size() == 0) {
            logger.info("没有答案");
            return new Result(false, "没有答案");
        }

        for (Answer answer : answers) {
            answerMapper.insertAnswer(answer);
        }

        return new Result(true, "保存成功");
    }
}
