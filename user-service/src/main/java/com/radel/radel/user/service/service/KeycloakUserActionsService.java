package com.radel.radel.user.service.service;

import java.util.List;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.radel.services.user.api.ChangeUserPasswordRequest;
import com.radel.services.user.api.UserActionResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KeycloakUserActionsService implements UserActionsService {

    private final RealmResource realmResource;
    private final String loginUrl;
    private final String clientId;
    private final int actionLinkLifespan;

    public KeycloakUserActionsService(RealmResource realmResource,
                                      @Value("${keycloak.users.loginUrl}") String loginUrl,
                                      @Value("${keycloak.users.clientId}") String clientId,
                                      @Value("${keycloak.users.actionLinkLifespan}") int actionLinkLifespan) {
        this.realmResource = realmResource;
        this.loginUrl = loginUrl;
        this.clientId = clientId;
        this.actionLinkLifespan = actionLinkLifespan;
    }

    @Override
    public UserActionResult executeUserActions(String userId, List<String> actions) {
        log.debug("Executing user actions: {userId: {}, actions: {}}", userId, actions);

        try {
            UserResource userResource = realmResource.users().get(userId);
            userResource.executeActionsEmail(clientId, loginUrl, actionLinkLifespan, actions);
        } catch (Exception e) {
            log.error("Unable to execute user actions: {actions: {}, errorReason: {}}", actions, e.getMessage());
            return new UserActionResult(false, null, e.getMessage());
        }

        log.info("Executed user actions: {userId: {}, actions: {}}", userId, actions);
        return new UserActionResult(true, null, null);
    }

    @Override
    public UserActionResult changeUserPassword(String userId, ChangeUserPasswordRequest request) {
        log.debug("Changing user password: {userId: {}}", userId);

        try {
            UserResource userResource = realmResource.users().get(userId);

            CredentialRepresentation newCredentials = new CredentialRepresentation();
            newCredentials.setType(CredentialRepresentation.PASSWORD);
            newCredentials.setValue(String.valueOf(request.getPassword()).trim());
            newCredentials.setTemporary(request.isTemporary());

            userResource.resetPassword(newCredentials);
        } catch (Exception e) {
            log.error("Unable to change user password: {errorReason: {}}", e.getMessage());
            return new UserActionResult(false, null, e.getMessage());
        }

        log.info("Changed user password: {userId: {}}", userId);
        return new UserActionResult(true, null, null);
    }
}
