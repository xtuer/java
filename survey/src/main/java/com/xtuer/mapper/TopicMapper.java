package com.xtuer.mapper;

import com.xtuer.bean.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TopicMapper {
    List<Topic> selectAllTopics();

    Topic selectTopicById(int id);
    void insertTopic(Topic topic);
    void updateTopic(Topic topic);
    void deleteTopic(int id);
    void updateOrder(@Param("id") int id, @Param("order") int order);
}
