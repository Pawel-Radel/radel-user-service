package com.radel.radel.user.service.configurations.beans;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.radel.radel.user.service.configurations.properties.KeycloakRealmResourceProperties;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({KeycloakRealmResourceProperties.class})
public class RealmResourceConfiguration {

    @Autowired
    private KeycloakRealmResourceProperties keycloakRealmResourceProperties;

    @Bean
    @ConditionalOnMissingBean
    public RealmResource realmResource() {
        Keycloak keycloak = Keycloak.getInstance(
                keycloakRealmResourceProperties.getServerUrl(),
                keycloakRealmResourceProperties.getAdminRealm(),
                keycloakRealmResourceProperties.getUsername(),
                keycloakRealmResourceProperties.getPassword(),
                keycloakRealmResourceProperties.getClientId());

        return keycloak.realm(keycloakRealmResourceProperties.getManagementRealm());
    }
}
