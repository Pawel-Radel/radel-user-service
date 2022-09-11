package com.radel.radel.user.service.service;

import static com.radel.radel.user.service.mapper.UserMapper.toUser;
import static com.radel.radel.user.service.mapper.UserMapper.toUserEntity;
import static com.radel.radel.user.service.mapper.UserMapper.updateUserEntity;
import static com.radel.radel.user.service.messaging.events.UserEventType.CREATE;
import static com.radel.radel.user.service.messaging.events.UserEventType.UPDATE;
import static java.util.Objects.isNull;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.radel.radel.user.service.domain.model.UserEntity;
import com.radel.radel.user.service.domain.repository.MongoUserRepository;
import com.radel.radel.user.service.messaging.events.UserEvent;
import com.radel.services.user.api.User;
import com.radel.services.user.api.UserActionResult;
import com.radel.services.user.api.UserEditableFieldEnum;
import com.radel.services.user.error.exception.UserException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("mongoUserManagementService")
@AllArgsConstructor
@Slf4j
public class MongoUserManagementService implements UserManagementService, UserPartialService {

    private final MongoUserRepository userRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public UserActionResult createUser(User managedUser) {
        log.debug("Creating user: {user: {}}", managedUser);

        UserActionResult userActionResult = save(managedUser);

        applicationEventPublisher.publishEvent(new UserEvent(this, userActionResult.getUser(), CREATE));

        log.info("Created user: {result: {}}", userActionResult);
        return userActionResult;
    }

    @Override
    public UserActionResult updateUser(String userId, User managedUser) {
        log.debug("Updating user: {data: {}}", managedUser);

        Optional<UserEntity> maybeUserEntity = userRepository.findByUserId(userId);

        if (maybeUserEntity.isEmpty()) {
            return UserActionResult.userDoesNotExist(userId);
        }

        UserEntity entity = maybeUserEntity.get();
        updateUserEntity(entity, managedUser);

        UserEntity updatedEntity = userRepository.save(entity);
        UserActionResult userActionResult = new UserActionResult(true, toUser(updatedEntity), null);

        applicationEventPublisher.publishEvent(new UserEvent(this, userActionResult.getUser(), UPDATE));

        log.info("Updated user: {userId: {}, success: {}}", userId, userActionResult.isSuccess());
        return userActionResult;
    }

    @Override
    public UserActionResult deleteUser(String userId) {
        log.debug("Deleting user: {userId: {}}", userId);

        userRepository.delete(userRepository.findByUserId(userId).orElseThrow( () -> UserException.userNotFound(userId)));
        log.info("Deleted user: {userId: {}}", userId);
        return new UserActionResult(true, null, null);
    }

    @Override
    public UserActionResult save(User user) {
        log.debug("Saving user: {user: {}}", user);

        UserEntity userEntity = toUserEntity(user);
        userRepository.upsert(userEntity);

        log.debug("Saved user {user: {}}", user);
        return new UserActionResult(true, user, null);
    }

    @Override
    public void updatePartial(String userId, User user, List<UserEditableFieldEnum> fields) {
        log.debug("Partially updating user: {user: {}, fields: {}}", user, fields);

        if (isNull(fields) || fields.isEmpty()) {
            return;
        }
        UserEntity userEntity = toUserEntity(user);

        userRepository.updatePartial(userId, userEntity, fields);
        log.debug("Partially updated user: {user: {}}", user);
    }
}
