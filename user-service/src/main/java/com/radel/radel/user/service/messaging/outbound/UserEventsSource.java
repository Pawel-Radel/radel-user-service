package com.radel.radel.user.service.messaging.outbound;

import static com.radel.radel.user.service.configurations.properties.SourceConfiguration.OUTPUT_BINDING_NAME_USER_EVENTS;
import static com.radel.radel.user.service.messaging.outbound.SourceConfiguration.OUTPUT_BINDING_MIME_TYPE;
import static java.util.Optional.ofNullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import com.radel.core.events.user.UserEvent;
import com.radel.radel.user.service.configurations.properties.OutputEventsProperties;
import com.radel.radel.user.service.service.UserChangesNotifier;
import com.radel.radel.user.service.service.UserQueryService;
import com.radel.services.user.api.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(OutputEventsProperties.class)
public class UserEventsSource implements UserChangesNotifier {

    private static final String EMPTY_STRING = "";
    private final UserQueryService userQueryService;
    private final StreamBridge streamBridge;
    private final OutputEventsProperties outputEvents;

    public void notifyUserChanges(String userId) {
        if (outputEvents.isEnabled()) {
            userQueryService.getUser(userId)
                    .ifPresent(user -> streamBridge.send(OUTPUT_BINDING_NAME_USER_EVENTS, toUserEvent(user), OUTPUT_BINDING_MIME_TYPE));
        }
    }

    private UserEvent toUserEvent(User user) {
        return UserEvent.newBuilder()
                .setUserId(user.getUserId())
                .setName(ofNullable(user.getName()).orElse(EMPTY_STRING))
                .setSurname(ofNullable(user.getSurname()).orElse(EMPTY_STRING))
                .setEmail(user.getEmail())
                .setCreatedTimestamp(ofNullable(user.getCreatedTimestamp()).map(Instant::ofEpochMilli).orElse(null))
                .setEnabled(user.getEnabled())
                .setEmailVerified(user.getEmailVerified())
                .setAttributes(ofNullable(user.getAttributes()).orElse(new HashMap<>(0)))
                .setRoles(ofNullable(user.getRoles()).orElse(new ArrayList<>(0)))
                .setGroups(ofNullable(user.getGroups()).orElse(new ArrayList<>(0)))
                .build();
    }
}

