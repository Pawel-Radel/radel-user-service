package com.radel.radel.user.service.service;

import com.radel.services.user.api.User;
import com.radel.services.user.api.UserActionResult;

public interface UserManagementService {

    UserActionResult createUser(User request);

    UserActionResult updateUser(String userId, User managedUser);

    UserActionResult deleteUser(String userId);

    UserActionResult save(User user);






}
