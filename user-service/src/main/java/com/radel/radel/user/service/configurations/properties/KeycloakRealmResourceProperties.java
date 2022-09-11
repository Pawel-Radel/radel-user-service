package com.radel.radel.user.service.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "keycloak.admin.client")
// @Configuration // usunąć jak coś
public class KeycloakRealmResourceProperties {

    private String serverUrl;

    private String adminRealm;

    private String username;

    private String password;

    private String clientId;

    private String managementRealm;
}
