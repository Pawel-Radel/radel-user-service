package com.radel.radel.user.service.service;

import java.util.List;

import com.radel.services.user.api.ChangeUserPasswordRequest;
import com.radel.services.user.api.UserActionResult;

public interface UserActionsService {

    UserActionResult executeUserActions(String userId, List<String> actions);

    UserActionResult changeUserPassword(String userId, ChangeUserPasswordRequest request);
}
