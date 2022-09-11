package com.radel.radel.user.service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.radel.radel.user.service.domain.model.UserEntity;
import com.radel.radel.user.service.domain.repository.MongoUserRepository;
import com.radel.radel.user.service.mapper.UserMapper;
import com.radel.radel.user.service.utils.UserQueryPredicate;
import com.radel.services.user.api.User;
import com.radel.services.user.api.UserSearchRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ReporitoryMongoUserQueryService implements UserQueryService {
    private final MongoUserRepository userRepository;

    @Override
    public Page<User> getUsers(UserSearchRequest request, Pageable pageable) {
        log.debug("Getting users {request: {}. pageable: {}}", request, pageable);

        Page<User> usersPage = userRepository.findAll(UserQueryPredicate.of(request), pageable)
                .map(UserMapper::toUser);

        log.debug("Got users {size: {}, totalElements: {}}", usersPage.getSize(), usersPage.getTotalElements());
        return usersPage;
    }

    @Override
    public List<User> getUsers(UserSearchRequest request) {
        log.info("Getting users {request: {}}", request);

        Iterable<UserEntity> userEntities = userRepository.findAll(UserQueryPredicate.of(request));

        List<User> users = StreamSupport.stream(userEntities.spliterator(), false)
                .map(UserMapper::toUser)
                .collect(Collectors.toList());

        log.debug("Got users {size: {}}", users.size());
        return users;
    }

    @Override
    public Optional<User> getUser(String userId) {
        log.debug("Getting user {userId: {}}", userId);

        Optional<UserEntity> maybeUserEntity = userRepository.findByUserId(userId);

        User user = null;
        if (maybeUserEntity.isPresent()) {
            user = UserMapper.toUser(maybeUserEntity.get());
        }

        log.debug("Got user {user: {}}", user);
        return Optional.ofNullable(user);
    }
}
