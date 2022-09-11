package com.radel.radel.user.service.keycloak.integration;

import static com.radel.radel.user.service.utils.RepresentationParser.parseGroupMembershipRepresentation;
import static com.radel.radel.user.service.utils.RepresentationParser.parseRealmRoleMappingRepresentation;
import static com.radel.radel.user.service.utils.RepresentationParser.parseUserEventRepresentation;
import static com.radel.services.user.api.UserEditableFieldEnum.EMAIL_VERIFIED;
import static com.radel.services.user.api.UserEditableFieldEnum.GROUPS;
import static com.radel.services.user.api.UserEditableFieldEnum.ROLES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.springframework.stereotype.Component;

import com.radel.radel.user.service.domain.representation.GroupMembershipRepresentation;
import com.radel.radel.user.service.domain.representation.RealmRoleMappingRepresentation;
import com.radel.radel.user.service.domain.representation.UserEventRepresentation;
import com.radel.radel.user.service.service.SynchronizationService;
import com.radel.radel.user.service.service.UserChangesNotifier;
import com.radel.radel.user.service.service.UserQueryService;
import com.radel.radel.user.service.utils.UserHelper;
import com.radel.services.user.api.User;
import com.radel.services.user.error.exception.UserException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KeycloakEventProcessor {

    private final UserQueryService userQueryService;

    private final SynchronizationService synchronizationService;

    private final UserChangesNotifier userChangesNotifier;

    public void processEvent(Event event) {
        log.info("Processing event: {eventType: {}}", event.getType());

        switch (event.getType()) {
            case VERIFY_EMAIL:
                handleVerifyEmailEvent(event);
                break;
            default:
                handleDefaultEvent(event);
                break;
        }

        userChangesNotifier.notifyUserChanges(event.getUserId());

        log.debug("Processed event: {event: {}}", event);
    }

    private void handleDefaultEvent(Event event) {
        synchronizationService.syncUser(event.getUserId());
    }

    private void handleVerifyEmailEvent(Event event) {
        User user = User.builder()
                .userId(event.getUserId())
                .emailVerified(true)
                .roles(emptyList())
                .groups(emptyList())
                .build();

        synchronizationService.syncUser(user, asList(EMAIL_VERIFIED));
    }

    public void processAdminEvent(AdminEvent adminEvent) {
        log.info("Processing admin event: {resourceType: {}, operationType: {}}", adminEvent.getResourceType(), adminEvent.getOperationType());

        String updatedUserId = null;

        switch (adminEvent.getResourceType()) {
            case REALM_ROLE_MAPPING:
                updatedUserId = handleRealmRoleMappingAdminEvent(adminEvent);
                break;
            case USER:
                updatedUserId = handleUserAdminEvent(adminEvent);
                break;
            case GROUP_MEMBERSHIP:
                updatedUserId = handleGroupMembershipAdminEvent(adminEvent);
                break;
            default:
                log.debug("Admin event ignored: {adminEvent: {}}", adminEvent);
                break;
        }

        ofNullable(updatedUserId)
                .ifPresent(userChangesNotifier::notifyUserChanges);

        log.debug("Processed admin event: {adminEvent: {}}", adminEvent);
    }

    private String handleGroupMembershipAdminEvent(AdminEvent adminEvent) {
        String userId = extractUserIdFromPath(adminEvent.getResourcePath());
        User user = getUser(userId);

        GroupMembershipRepresentation representation = parseGroupMembershipRepresentation(adminEvent);

        UserHelper.updateGroup(user, representation.getId(), adminEvent.getOperationType());
        synchronizationService.syncUser(user, asList(GROUPS));

        return userId;
    }

    private String handleUserAdminEvent(AdminEvent adminEvent) {
        String userId = extractUserIdFromPath(adminEvent.getResourcePath());

        switch (adminEvent.getOperationType()) {
            case CREATE:
                synchronizationService.syncUser(userId);
                break;
            case UPDATE: {
                UserEventRepresentation representation = parseUserEventRepresentation(adminEvent);
                synchronizationService.syncUser(representation.toUser(), UserEventRepresentation.getFields());
            }
            break;
            case DELETE:
                synchronizationService.deleteUser(userId);
                break;
        }

        return userId;
    }

    private String handleRealmRoleMappingAdminEvent(AdminEvent adminEvent) {
        String userId = extractUserIdFromPath(adminEvent.getResourcePath());
        User user = getUser(userId);

        List<String> values = parseRealmRoleMappingRepresentation(adminEvent)
                .stream()
                .map(RealmRoleMappingRepresentation::getName)
                .collect(toList());

        UserHelper.updateRoles(user, values, adminEvent.getOperationType());
        synchronizationService.syncUser(user, asList(ROLES));

        return userId;
    }

    private String extractUserIdFromPath(String path) {
        int usersIdx = path.indexOf("users");
        int idStartIdx = path.indexOf("/", usersIdx);
        int idEndIdx = path.indexOf("/", idStartIdx + 1);

        return idEndIdx == -1
                ? path.substring(idStartIdx + 1)
                : path.substring(idStartIdx + 1, idEndIdx);
    }

    private User getUser(String userId) {
        return userQueryService.getUser(userId)
                .orElseThrow(() -> UserException.userNotFound(userId));
    }
}
