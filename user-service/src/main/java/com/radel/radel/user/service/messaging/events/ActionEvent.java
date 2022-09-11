package com.radel.radel.user.service.messaging.events;

import java.util.List;

import org.springframework.context.ApplicationEvent;

public class ActionEvent extends ApplicationEvent {

    private String userId;

    List<String> actionsToExecute;

    public ActionEvent(Object source, String userId, List<String> actionsToExecute) {
        super(source);
        this.userId = userId;
        this.actionsToExecute = actionsToExecute;
    }
    
}
