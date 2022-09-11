package com.radel.radel.user.service.service;

import java.util.List;

import com.radel.services.user.api.User;
import com.radel.services.user.api.UserEditableFieldEnum;

public interface SynchronizationService {

     void syncUser(User user);

     void syncUser(String userId);

     void deleteUser(String userId);

     void syncUser(User user, List<UserEditableFieldEnum> fields);
}
