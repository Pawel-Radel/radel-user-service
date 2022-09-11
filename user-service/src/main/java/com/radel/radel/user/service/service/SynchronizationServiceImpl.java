package com.radel.radel.user.service.service;

import static com.radel.radel.user.service.mapper.UserMapper.toAuthUser;
import static com.radel.radel.user.service.mapper.UserMapper.toUserGroups;
import static com.radel.radel.user.service.mapper.UserMapper.toUserRoles;

import java.util.List;
import java.util.Set;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.radel.services.user.api.User;
import com.radel.services.user.api.UserEditableFieldEnum;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SynchronizationServiceImpl implements SynchronizationService {

    private final RealmResource realmResource;

    private final UserManagementService userManagementService;

    private final UserPartialService userPartialService;

    private final Set<String> managedRoles;

    public SynchronizationServiceImpl(RealmResource realmResource,
                                      @Qualifier("mongoUserManagementService") UserManagementService userManagementService,
                                      Set<String> managedRoles,
                                      UserPartialService userPartialService) {
        this.realmResource = realmResource;
        this.userManagementService = userManagementService;
        this.managedRoles = managedRoles;
        this.userPartialService = userPartialService;
    }

    @Override
    public void syncUser(User user) {
        log.debug("Starting keycloak user synchronization {user: {}}", user);
        userManagementService.save(user);
        log.debug("Keycloak user synchronization finished");
    }

    @Override
    public void syncUser(String userId) {
        log.debug("Starting keycloak user synchronization {userId: {}}", userId);

        UserResource userResource = realmResource.users().get(userId);
        User user = toAuthUser(userResource.toRepresentation(), toUserRoles(userResource, managedRoles), toUserGroups(userResource));

        userManagementService.save(user);
        log.debug("Keycloak user synchronization finished");
    }

    public void syncUser(User user, List<UserEditableFieldEnum> fields) {
        log.debug("Starting keycloak user synchronization {user: {}, fields: {}}", user, fields);
        userPartialService.updatePartial(user.getUserId(), user, fields);
        log.debug("Keycloak user synchronization finished");
    }

    @Override
    public void deleteUser(String userId) {
        log.debug("Deleting user {userId: {}}", userId);

        userManagementService.deleteUser(userId);

        log.debug("Deleted user {userId: {}}", userId);
    }
}
