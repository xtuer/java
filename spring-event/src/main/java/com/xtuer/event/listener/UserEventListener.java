package com.xtuer.event.listener;

import com.alibaba.fastjson.JSON;
import com.xtuer.event.event.UserEvent;
import org.springframework.context.ApplicationListener;

public class UserEventListener implements ApplicationListener<UserEvent> {
    public void onApplicationEvent(UserEvent event) {
        System.out.printf("Event received: %s - %s\n", event.getEventType(), JSON.toJSONString(event.getUser()));
    }
}
