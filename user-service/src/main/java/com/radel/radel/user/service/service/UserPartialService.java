package com.radel.radel.user.service.service;

import java.util.List;

import com.radel.radel.user.service.domain.model.UserEntity;
import com.radel.services.user.api.User;
import com.radel.services.user.api.UserEditableFieldEnum;

public interface UserPartialService {

    public void updatePartial(String userId, User user, List<UserEditableFieldEnum> fields);
}
