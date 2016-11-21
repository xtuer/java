package com.xtuer.event.event;

import com.xtuer.bean.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserEvent extends ApplicationEvent {
    private String eventType;
    private User user;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public UserEvent(Object source, String eventType, User user) {
        super(source);
        this.eventType = eventType;
        this.user = user;
    }
}
