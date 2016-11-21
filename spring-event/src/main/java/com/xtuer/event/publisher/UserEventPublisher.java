package com.xtuer.event.publisher;

import com.xtuer.bean.User;
import com.xtuer.event.event.UserEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class UserEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        // Spring 容器在创建 UserEventPublisher 对象时自动注入 applicationEventPublisher
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(User user, String action) {
        applicationEventPublisher.publishEvent(new UserEvent(this, action, user));
    }
}
