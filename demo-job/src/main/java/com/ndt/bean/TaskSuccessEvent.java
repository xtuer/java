package com.ndt.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Task 执行成功的事件
 */
@Getter
@Setter
public class TaskSuccessEvent extends ApplicationEvent {
    public TaskSuccessEvent(Task source) {
        super(source);
    }
}
