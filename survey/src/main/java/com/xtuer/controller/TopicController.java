package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.bean.Topic;
import com.xtuer.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class TopicController {
    private static Logger logger = LoggerFactory.getLogger(TopicController.class.getName());

    @Autowired
    private TopicService topicService;

    /**
     * 访问管理所有 topics 的页面
     * @return
     */
    @GetMapping(UriViewConstants.URI_ADMIN_TOPICS)
    public String adminTopicsPage() {
        return UriViewConstants.VIEW_ADMIN_TOPICS;
    }

    /**
     * 获取所有 topics 的 JSON 表示字符串
     * @return 所有 topics 的 JSON 字符串
     */
    @GetMapping(UriViewConstants.REST_TOPICS)
    @ResponseBody
    public List<Topic> selectAllTopics() {
        return topicService.selectAllTopics();
    }

    /**
     * 使用传入的 topic 查找 topic
     * @param topicId topic 的 id
     * @return topic 的 JSON 字符串
     */
    @GetMapping(UriViewConstants.REST_TOPICS_WITH_ID)
    @ResponseBody
    public Topic selectTopic(@PathVariable int topicId) {
        return topicService.selectTopicById(topicId);
    }

    /**
     * 创建 topic, topic 的内容从 request 的 body 里取得
     * @param topic 要创建的 topic 对象
     * @return 创建结果的 Result 的表示, 自动的转换为 JSON
     */
    @PostMapping(UriViewConstants.REST_TOPICS)
    @ResponseBody
    public Result insertTopic(@RequestBody Topic topic) {
        return topicService.insertTopic(topic);
    }

    /**
     * 更新 topic, topic 的内容从 request 的 body 里取得
     * @param topic 要更新的 topic 对象
     * @return 更新结果的 Result 的表示, 自动的转换为 JSON
     */
    @PutMapping(UriViewConstants.REST_TOPICS_WITH_ID)
    @ResponseBody
    public Result updateTopic(@RequestBody Topic topic) {
        return topicService.updateTopic(topic);
    }

    /**
     * 删除 topic
     * @param topicId topic 的 id
     * @return 删除结果的 Result 的表示, 自动的转换为 JSON
     */
    @DeleteMapping(UriViewConstants.REST_TOPICS_WITH_ID)
    @ResponseBody
    public Result deleteTopic(@PathVariable int topicId) {
        return topicService.deleteTopic(topicId);
    }

    @PutMapping(UriViewConstants.REST_TOPICS_ORDERS)
    @ResponseBody
    public Result updateTopicsOrders(@RequestBody List<Map<String, Integer>> ordersMap) {
        return topicService.updateTopicOrders(ordersMap);
    }

    /**
     * 取得 topic 下每个问题回答的个数
     * @return
     */
    @GetMapping(value = UriViewConstants.REST_QUESTIONS_ANSWERS_STATISTICS)
    @ResponseBody
    public List<Map<String, String>> topicAnswersStatistic(@PathVariable int topicId) {
        return topicService.topicAnswersStatistic(topicId);
    }
}
