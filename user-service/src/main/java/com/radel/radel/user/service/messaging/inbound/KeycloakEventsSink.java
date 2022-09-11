package com.radel.radel.user.service.messaging.inbound;

import java.util.function.Consumer;

import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.radel.radel.user.service.keycloak.integration.KeycloakEventProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class KeycloakEventsSink {

    private final KeycloakEventProcessor keycloakEventProcessor;

    @Bean
    public Consumer<Event> sinkEvent() {
        return keycloakEventProcessor::processEvent;
    }

    @Bean
    public Consumer<AdminEvent> sinkAdminEvent() {
        return keycloakEventProcessor::processAdminEvent;
    }
}
