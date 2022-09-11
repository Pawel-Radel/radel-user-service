package com.radel.radel.user.service.configurations.properties;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Builder;
import lombok.Data;


@Data
// @Builder
@ConfigurationProperties(prefix = "keycloak.users.on-create.actions")
public class OnCreateUserActionsProperties {

    /**
     * Is actions enabled. Default is true
     */
    private boolean enabled;

    /**
     * List of actions to execute when User is created. Default is VERIFY_EMAIL.
     */
    private List<String> actions;
}
