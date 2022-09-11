package com.radel.radel.user.service.messaging.events;

import org.springframework.context.ApplicationEvent;

import com.radel.services.user.api.User;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserEvent extends ApplicationEvent {

    private User user;
    private UserEventType type;

    public UserEvent(Object source, User user, UserEventType type) {
        super(source);
        this.user = user;
        this.type = type;
    }
}
