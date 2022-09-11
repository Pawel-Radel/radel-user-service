package com.radel.radel.user.service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.radel.services.user.api.User;
import com.radel.services.user.api.UserSearchRequest;

public interface UserQueryService {

    Page<User> getUsers(UserSearchRequest request, Pageable pageable);

    List<User> getUsers(UserSearchRequest request);

    Optional<User> getUser(String userId);
}
