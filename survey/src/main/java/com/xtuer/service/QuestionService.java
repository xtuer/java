package com.xtuer.service;

import com.xtuer.mapper.AnswerMapper;
import com.xtuer.mapper.QuestionItemMapper;
import com.xtuer.mapper.QuestionMapper;
import com.xtuer.bean.Question;
import com.xtuer.bean.QuestionItem;
import com.xtuer.bean.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 访问 Question 的服务, 可以验证 Question 的有效性, 对 Question 进行 CURD 操作.
 */
@Service
public class QuestionService {
    private static Logger logger = LoggerFactory.getLogger(QuestionService.class.getName());

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionItemMapper questionItemMapper;

    @Autowired
    private AnswerMapper answerMapper;

    public List<Question> selectQuestionsByTopicId(int topicId) {
        return questionMapper.selectQuestionsByTopicId(topicId);
    }

    public Question selectQuestion(int id) {
        return questionMapper.selectQuestion(id);
    }

    @Transactional
    public Result insertQuestion(Question question) {
        // 校验问题的合法性
        Result result = validateQuestion(question);

        if (!result.isSuccess()) {
            logger.info(result.getMessage());
            return result;
        }

        questionMapper.insertQuestion(question); // 1. 插入 question
        insertQuestionItems(question); // 2. 插入问题选项

        return new Result(true, question.getId(), "插入成功");
    }

    @Transactional
    public Result updateQuestion(Question question) {
        // 校验问题的合法性
        Result result = validateQuestion(question);

        if (!result.isSuccess()) {
            logger.info(result.getMessage());
            return result;
        }

        questionMapper.updateQuestion(question); // 1. 更新 question
        insertQuestionItems(question); // 2. 插入问题选项

        return new Result(true, "更新成功");
    }

    @Transactional
    public Result deleteQuestion(int id) {
        questionMapper.deleteQuestion(id);
        questionItemMapper.deleteQuestionItemsByQuestionId(id);
        return new Result(true, "删除成功");
    }

    /**
     * 删除问题, 同时删除问题的所有选项
     *
     * @param topicId topic's id
     * @return 成功返回 true, 否则返回 false
     */
    @Transactional
    public Result deleteQuestionsByTopicId(int topicId) {
        // 1. 删除问题的选项
        List<Integer> questionIds = questionMapper.selectQuestionIdsByTopicId(topicId);

        if (questionIds != null && questionIds.size() > 0) {
            questionItemMapper.deleteQuestionItemsByQuestionIds(questionIds);
        }

        // 2. 删除问题
        questionMapper.deleteQuestionsByTopicId(topicId);

        return new Result(true, "删除成功");
    }

    @Transactional
    public Result updateQuestionOrders(List<Map<String, Integer>> orders) {
        for (Map<String, Integer> order : orders) {
            if (order.size() > 0) {
                questionMapper.updateOrder(order.get("questionId").intValue(), order.get("order"));
            }
        }

        return new Result(true, "顺序保存成功");
    }

    public List<String> selectQuestionAnswerInputs(int questionId, int questionItemId, int offset) {
        return answerMapper.selectQuestionAnswerInputs(questionId, questionItemId, offset);
    }

    /**
     * 校验问题的合法性:
     *     1. question 的 content 不能为空
     *     2. 建议和描述不需要选项
     *     3. 单选题和多选题必须要有选项, 且 questionItem 的 content 不能为空
     *
     * @param question 问题
     * @return 合法返回 true, 否则返回 false
     */
    private Result validateQuestion(Question question) {
        if (question == null) {
            return new Result(false, "问题不能为 null");
        }

        if (StringUtils.isEmpty(question.getContent())) {
            return new Result(false, "问题的描述不能为空");
        }

        if (Question.TYPE_SUGGESTION == question.getType() || Question.TYPE_DESCRIPTION == question.getType()) {
            return new Result(true, "");
        }

        if (question.getItems() == null) {
            return new Result(false, "不能没有选项");
        }

        for (QuestionItem item : question.getItems()) {
            if (StringUtils.isEmpty(item.getContent())) {
                return new Result(false, "选项不能为空");
            }
        }

        return new Result(true, "问题是有效的");
    }

    /**
     * 插入新的问题选项到数据库
     *     1. 删除已有的选项
     *     2. 插入新的选项
     *
     * @param question 问题
     */
    private void insertQuestionItems(Question question) {
        questionItemMapper.deleteQuestionItemsByQuestionId(question.getId());

        if (null == question.getItems()) {
            return;
        }

        // 可优化为批量更新
        for (QuestionItem item : question.getItems()) {
            item.setQuestionId(question.getId());
            questionItemMapper.insert(item);
        }
    }
}
