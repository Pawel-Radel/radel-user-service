package com.radel.radel.user.service.service;

import static com.radel.radel.user.service.mapper.UserMapper.toUserGroups;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.radel.radel.user.service.configurations.properties.OnCreateUserActionsProperties;
import com.radel.services.user.api.User;
import com.radel.services.user.api.UserActionResult;

import lombok.extern.slf4j.Slf4j;

@Service("keycloakUserManagementService")
@Slf4j
@EnableConfigurationProperties(OnCreateUserActionsProperties.class)
public class KeycloakUserManagementService implements UserManagementService {

    private static final String EMPTY_STRING = "";
    private final RealmResource realmResource;
    private final OnCreateUserActionsProperties afterCreateActions;

    private final UserActionsService userActionsService;
    private final Set<String> managedRoles;
    private final String clientId;

    private final UserManagementService decoratedUserManagementService;

    //   private final ApplicationEventPublisher applicationEventPublisher;

    public KeycloakUserManagementService(RealmResource realmResource,
                                         OnCreateUserActionsProperties afterCreateActions,
                                         @Value("${keycloak.users.managed-roles}") Set<String> managedRoles,
                                         @Value("${keycloak.users.clientId}") String clientId,
                                         @Qualifier("mongoUserManagementService") UserManagementService decoratedUserManagementService,
                                         //   ApplicationEventPublisher applicationEventPublisher,
                                         UserActionsService userActionsService) {
        this.realmResource = realmResource;
        this.afterCreateActions = afterCreateActions;
        this.managedRoles = managedRoles;
        this.clientId = clientId;
        this.decoratedUserManagementService = decoratedUserManagementService;
        //  this.applicationEventPublisher = applicationEventPublisher;
        this.userActionsService = userActionsService;
    }

    @Override
    public UserActionResult createUser(User managedUser) {
        log.debug("Creating user: {user: {}}", managedUser);

        UserRepresentation user = new UserRepresentation();
        user.setFirstName(managedUser.getName());
        user.setLastName(managedUser.getSurname());
        user.setEmail(managedUser.getEmail());
        user.setEnabled(managedUser.getEnabled());
        user.setEmailVerified(managedUser.getEmailVerified());

        Map<String, List<String>> attributes = toInputAttributes(managedUser.getAttributes());
        user.setAttributes(attributes);

        Response response = realmResource.users().create(user);
        UserActionResult userActionResult = handleCreateUserResponse(managedUser, response);

        log.info("Created user: {result: {}}", userActionResult);
        return userActionResult;
    }

    @Override
    public UserActionResult updateUser(String userId, User managedUser) {
        log.debug("Updating user: {userId: {}, data: {}}", userId, managedUser);

        managedUser.setUserId(userId);
        UserResource userResource = realmResource.users().get(userId);
        updateUserRepresentation(managedUser, userResource);

        UserActionResult userActionResult = updateUserRolesAndGroups(managedUser);


        // czy to potrzebne?
/*        if (userActionResult.isSuccess()) {
            decoratedUserManagementService.updateUser(userId, managedUser);
        }*/

        log.info("Updated user: {userId: {}, success: {}}", userId, userActionResult.isSuccess());
        return userActionResult;
    }

    @Override
    public UserActionResult deleteUser(String userId) {
        log.debug("Deleting user: {userId: {}}", userId);

        Response response = realmResource.users().delete(userId);
        UserActionResult userActionResult = handleDeleteUserResponse(response);


        // Czy to potrzebne
/*        if (userActionResult.isSuccess()) {
            decoratedUserManagementService.deleteUser(userId);
        }*/

        log.info("Deleted user: {userId: {}, success: {}}", userId, userActionResult.isSuccess());
        return userActionResult;
    }

    @Override
    public UserActionResult save(User user) {
        log.debug("Saving user: {user: {}}", user);

        boolean exists = realmResource.users().count("id:" + user.getUserId()) > 0;

        UserActionResult result;
        if (exists) {
            result = updateUser(user.getUserId(), user);
        } else {
            result = createUser(user);
        }

        log.info("Saved user: {result: {}}", result);
        return result;
    }

    private UserActionResult handleDeleteUserResponse(Response response) {
        if (Response.Status.NO_CONTENT.getStatusCode() == response.getStatus()) {
            return new UserActionResult(true, null, null);
        }

        return new UserActionResult(false, null, String.valueOf(response.getStatus()));
    }

    private UserActionResult handleCreateUserResponse(User managedUser, Response response) {

        if (Response.Status.CREATED.getStatusCode() == response.getStatus()) {
            managedUser.setUserId(getCreatedId(response));

            UserActionResult userActionResult = updateUserRolesAndGroups(managedUser);

            if (!userActionResult.isSuccess()) {
                log.error("Handle created user actions (assign roles and groups) failed: {reason: {}}. User will be deleted!", userActionResult.getErrorReason());
                realmResource.users().delete(managedUser.getUserId());
            }

            if (afterCreateActions.isEnabled()) {
                userActionsService.executeUserActions(userActionResult.getUser().getUserId(), afterCreateActions.getActions());
            }

            return userActionResult;
        }

        return new UserActionResult(false, null, String.valueOf(response.getStatus()));
    }

    private UserActionResult updateUserRolesAndGroups(User managedUser) {

        UserResource userResource;

        try {
            userResource = realmResource.users().get(managedUser.getUserId());
            updateUserRoles(managedUser, userResource);
            updateUserGroups(managedUser, userResource);
            return new UserActionResult(true, managedUser, null);
        } catch (Exception e) {

            log.error("Update user actions (assign roles and groups) failed: {reason: {}}", e.getMessage());
            e.printStackTrace();
            return new UserActionResult(false, null, e.getMessage());
        }
    }

    private void updateUserRepresentation(User managedUser, UserResource userResource) {
        UserRepresentation representation = userResource.toRepresentation();

        boolean needToUpdate = false;

        if (!ofNullable(representation.getFirstName()).orElse(EMPTY_STRING).equals(managedUser.getName())) {
            representation.setFirstName(managedUser.getName());
            needToUpdate = true;
        }

        if (!ofNullable(representation.getLastName()).orElse(EMPTY_STRING).equals(managedUser.getSurname())) {
            representation.setLastName(managedUser.getSurname());
            needToUpdate = true;
        }

        if (!representation.getEmail().equals(managedUser.getEmail())) {
            representation.setEmail(managedUser.getEmail());
            needToUpdate = true;
        }

        if (representation.isEnabled().compareTo(managedUser.getEnabled()) != 0) {
            representation.setEnabled(managedUser.getEnabled());
            needToUpdate = true;
        }

        if (representation.isEmailVerified().compareTo(managedUser.getEmailVerified()) != 0) {
            representation.setEmailVerified(managedUser.getEmailVerified());
            needToUpdate = true;
        }

        if (nonNull(managedUser.getAttributes())) {

            if (isNull(representation.getAttributes())) {
                representation.setAttributes(new HashMap<>());
            }

            Map<String, List<String>> attributes = toInputAttributes(managedUser.getAttributes());

            if (!representation.getAttributes().equals(attributes)) {
                representation.setAttributes(attributes);
                needToUpdate = true;
            }

        }

        if (needToUpdate) {
            userResource.update(representation);
            if (!representation.isEnabled()) {
                logoutUser(userResource);
            }
        }
    }

    private void logoutUser(UserResource userResource) {
        try {
            userResource.logout();
            userResource.revokeConsent(clientId);
        } catch (Exception e) {
            log.warn("Unable to log out user: {reason: {}}", e.getMessage());
        }

    }

    private void updateUserGroups(User managedUser, UserResource userResource) {

        List<String> userGroups = toUserGroups(userResource);

        userGroups.stream()
                .filter(groupId -> !managedUser.getGroups().contains(groupId))
                .forEach(userResource::leaveGroup);

        managedUser.getGroups()
                .stream()
                .filter(groupId -> !userGroups.contains(groupId))
                .forEach(userResource::joinGroup);
    }

    private void updateUserRoles(User managedUser, UserResource userResource) {

        validateRoles(managedUser.getRoles());

        List<RoleRepresentation> grantedRoles = userResource.roles().realmLevel().listAll();
        Set<String> grantedRolesNames = userResource.roles().realmLevel().listAll()
                .stream().map(RoleRepresentation::getName)
                .collect(Collectors.toSet());

        Set<String> rolesToGrant = managedUser.getRoles()
                .stream()
                .filter(role -> managedRoles.contains(role) && !grantedRolesNames.contains(role))
                .collect(Collectors.toSet());

        List<RoleRepresentation> rolesToDelete = grantedRoles
                .stream()
                .filter(role -> managedRoles.contains(role.getName()) && !managedUser.getRoles().contains(role.getName()))
                .collect(toList());

        if (!rolesToDelete.isEmpty()) {
            userResource.roles().realmLevel().remove(rolesToDelete);
        }

        if (!rolesToGrant.isEmpty()) {
            List<RoleRepresentation> rolesToAdd = rolesToGrant.stream()
                    .map(role -> realmResource.roles().get(role).toRepresentation())
                    .collect(toList());
            userResource.roles().realmLevel().add(rolesToAdd);
        }
    }

    private User toAuthUser(UserRepresentation userRepresentation, List<String> roles, List<String> groups) {
        Map<String, String> attributes = toOutputAttributes(userRepresentation.getAttributes());

        return User.builder()
                .userId(userRepresentation.getId())
                .email(userRepresentation.getEmail())
                .name(ofNullable(userRepresentation.getFirstName()).orElse(EMPTY_STRING))
                .surname(ofNullable(userRepresentation.getLastName()).orElse(EMPTY_STRING))
                .createdTimestamp(userRepresentation.getCreatedTimestamp())
                .enabled(userRepresentation.isEnabled())
                .emailVerified(userRepresentation.isEmailVerified())
                .attributes(attributes)
                .roles(roles)
                .groups(groups)
                .build();
    }

    private Map<String, String> toOutputAttributes(Map<String, List<String>> attributes) {
        if (isNull(attributes)) {
            return new HashMap<>();
        }

        return attributes.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().stream().findFirst().orElse(EMPTY_STRING)));
    }

    private Map<String, List<String>> toInputAttributes(Map<String, String> attributes) {
        if (isNull(attributes)) {
            return new HashMap<>();
        }

        return attributes.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> Collections.singletonList(entry.getValue())));
    }

    private void validateRoles(List<String> roles) {
        if (!managedRoles.containsAll(roles)) {
            log.error("An error has occurred! Requested roles contains wrong mapping names {roles: {}}", roles);
            throw new IllegalArgumentException(format("Requested roles contains wrong mapping names {roles: %s}", roles));
        }
    }
}
