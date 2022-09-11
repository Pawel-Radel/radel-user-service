package com.radel.radel.user.service.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.radel.services.user.api.User;
import com.radel.services.user.api.UserActionResult;


@Service("userManagementServiceImpl")
public class UserManagementServiceImpl implements UserManagementService {


    private UserManagementService keycloakUserManagementSystem;

    private UserManagementService mongoUserManagementSystem;

    public UserManagementServiceImpl(@Qualifier("keycloakUserManagementService") UserManagementService keycloakUserManagementService,
                                     @Qualifier("mongoUserManagementService") UserManagementService mongoUserManagementSystem) {
        this.keycloakUserManagementSystem = keycloakUserManagementService;
        this.mongoUserManagementSystem = mongoUserManagementSystem;
    }

    @Override
    public UserActionResult createUser(User request) {
        return keycloakUserManagementSystem.createUser(request);
    }

    @Override
    public UserActionResult updateUser(String userId, User managedUser) {
        return keycloakUserManagementSystem.updateUser(userId, managedUser);
    }

    @Override
    public UserActionResult deleteUser(String userId) {
        return keycloakUserManagementSystem.deleteUser(userId);
    }

    @Override
    public UserActionResult save(User user) {
        return null;
    }
}
