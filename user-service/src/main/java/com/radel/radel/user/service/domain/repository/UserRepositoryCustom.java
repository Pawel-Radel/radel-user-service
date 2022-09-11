package com.radel.radel.user.service.domain.repository;

import java.util.List;

import com.mongodb.client.result.UpdateResult;
import com.radel.radel.user.service.domain.model.UserEntity;
import com.radel.services.user.api.UserEditableFieldEnum;

public interface UserRepositoryCustom {

    void upsertAll(Iterable<UserEntity> users);

    UpdateResult upsert(UserEntity user);

    void updatePartial(String userId, UserEntity userEntity, List<UserEditableFieldEnum> fields);
}
