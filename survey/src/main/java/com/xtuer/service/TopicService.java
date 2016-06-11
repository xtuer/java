package com.xtuer.service;

import com.xtuer.mapper.AnswerMapper;
import com.xtuer.mapper.TopicMapper;
import com.xtuer.bean.Question;
import com.xtuer.bean.QuestionItem;
import com.xtuer.bean.Result;
import com.xtuer.bean.Topic;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class TopicService {
    private static Logger logger = LoggerFactory.getLogger(TopicService.class.getName());

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerMapper answerMapper;

    public List<Topic> selectAllTopics() {
        return topicMapper.selectAllTopics();
    }

    public Topic selectTopicById(int topicId) {
        Topic t = topicMapper.selectTopicById(topicId);
        return topicMapper.selectTopicById(topicId);
    }

    /**
     * 查找 topic 和它的所有 questions
     * @param topicId topic 的 id
     * @return Topic 对象
     */
    public Topic selectTopicAndQuestions(int topicId) {
        Topic topic = topicMapper.selectTopicById(topicId);

        if (topic == null) {
            logger.info("主题 {} 不存在", topicId);
            return null;
        }

        topic.setQuestions(questionService.selectQuestionsByTopicId(topicId));

        // 把 question 和 question item 的 list 不存在时初始化为空的 list, 可以避免在使用时的空指针判断
        List<Question> questions = topic.getQuestions();

        if (questions == null) {
            logger.info("主题 {} 没有问题", topicId);
            questions = new LinkedList<Question>();
            topic.setQuestions(questions);
        }

        for (Question question : questions) {
            List<QuestionItem> items = question.getItems();

            if (items == null) {
                logger.info("问题 {} 没有选项", question.getId());
                question.setItems(new LinkedList<QuestionItem>());
            }
        }

        return topic;
    }

    /**
     * 创建 Topic
     *
     * @param topic
     * @return
     */
    public Result insertTopic(Topic topic) {
        Result result = validateTopic(topic);

        if (!result.isSuccess()) {
            logger.info(result.getMessage());
            return result;
        }

        topicMapper.insertTopic(topic);
        return new Result(true, topic.getId(), "插入成功");
    }

    /**
     * 更新 Topic
     *
     * @param topic
     * @return
     */
    public Result updateTopic(Topic topic) {
        Result result = validateTopic(topic);

        if (!result.isSuccess()) {
            logger.info(result.getMessage());
            return result;
        }

        topicMapper.updateTopic(topic);
        return new Result(true, "更新成功");
    }

    /**
     * 删除 Topic, 以及属于它的 Question, Question Items
     *
     * @param topicId
     * @return
     */
    @Transactional
    public Result deleteTopic(int topicId) {
        topicMapper.deleteTopic(topicId);
        questionService.deleteQuestionsByTopicId(topicId);

        return new Result(true, "删除成功");
    }

    /**
     * 取得 Topic 的 url
     *
     * @param topicId
     * @return
     */
    public String getTopicUrl(int topicId) {
        Topic topic = selectTopicById(topicId);
        String url = StringUtils.trim(topic.getUrl());

        return url;
    }

    /**
     * 更新 Topic 的顺序
     *
     * @param orders
     * @return
     */
    @Transactional
    public Result updateTopicOrders(List<Map<String, Integer>> orders) {
        for (Map<String, Integer> order : orders) {
            if (order != null && order.size() > 0) {
                topicMapper.updateOrder(order.get("topicId").intValue(), order.get("order"));
            }
        }

        return new Result(true, "顺序保存成功");
    }

    public List<Map<String, String>> topicAnswersStatistic(int topicId) {
        return answerMapper.topicAnswersStatistic(topicId);
    }

    /**
     * 验证 Topic 的内容是否有效, content 不能为空或空白字符串
     *
     * @param topic
     * @return
     */
    private Result validateTopic(Topic topic) {
        if (topic == null) {
            return new Result(false, "Topic 不能为 null");
        }

        if (StringUtils.isBlank(topic.getContent())) {
            return new Result(false, "Topic 的 content 不能为空");
        }

        return new Result(true, "Topic 是有效的");
    }
}
