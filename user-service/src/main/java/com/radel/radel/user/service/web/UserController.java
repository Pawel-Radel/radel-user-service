package com.radel.radel.user.service.web;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radel.core.events.PawelTestEvent;
import com.radel.radel.user.service.messaging.outbound.TenantCreateSource;
import com.radel.radel.user.service.service.UserActionsService;
import com.radel.radel.user.service.service.UserManagementService;
import com.radel.radel.user.service.service.UserQueryService;
import com.radel.radel.user.service.utils.UserSearchRequestAttributesExtractor;
import com.radel.services.user.api.ChangeUserPasswordRequest;
import com.radel.services.user.api.User;
import com.radel.services.user.api.UserAction;
import com.radel.services.user.api.UserActionResult;
import com.radel.services.user.api.UserSearchRequest;
import com.radel.services.user.error.exception.UserException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/management")
@Slf4j
public class UserController {

    private final TenantCreateSource tenantCreateSource;

    private final UserManagementService keycloakUserManagementService;

    private final UserQueryService userQueryService;

    private final UserActionsService userActionsService;

    public UserController(TenantCreateSource tenantCreateSource,
                          @Qualifier("keycloakUserManagementService") UserManagementService keycloakUserManagementService,
                          UserQueryService userQueryService,
                          UserActionsService userActionsService) {

        this.tenantCreateSource = tenantCreateSource;
        this.keycloakUserManagementService = keycloakUserManagementService;
        this.userQueryService = userQueryService;
        this.userActionsService = userActionsService;
    }

    @GetMapping("/test")
    String getUsers() {

        PawelTestEvent e = new PawelTestEvent("dupa");

        log.error("{}", e);

        tenantCreateSource.sendTenantCreateEvent(new PawelTestEvent("dupa"));

        return "dua";
    }

    @PostMapping
    public UserActionResult create(@RequestBody @Valid User request) {
        return keycloakUserManagementService.createUser(request);
    }

    @PutMapping("/{userId}")
    public UserActionResult update(@PathVariable("userId") String userId, @RequestBody @Valid User request) {
        return keycloakUserManagementService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public UserActionResult delete(@PathVariable("userId") String userId) {
        return keycloakUserManagementService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable("userId") String userId) {
        return userQueryService.getUser(userId)
                .orElseThrow(() -> UserException.userNotFound(userId));
    }

    @GetMapping
    public Page<User> get(HttpServletRequest httpRequest, UserSearchRequest userSearchRequest, Pageable pageable) {
        Map<String, String> attributes = UserSearchRequestAttributesExtractor.extract(httpRequest);
        userSearchRequest.setAttributes(attributes);

        return userQueryService.getUsers(userSearchRequest, pageable);
    }

    @GetMapping("/list")
    public List<User> get(HttpServletRequest httpRequest, UserSearchRequest userSearchRequest) {
        Map<String, String> attributes = UserSearchRequestAttributesExtractor.extract(httpRequest);
        userSearchRequest.setAttributes(attributes);

        return userQueryService.getUsers(userSearchRequest);
    }

    @PutMapping("/{userId}/actions/{action}")
    public UserActionResult executeAction(@PathVariable String userId, @PathVariable UserAction action) {
        return userActionsService.executeUserActions(userId, Collections.singletonList(action.toString()));
    }

    @PutMapping("/{userId}/password")
    public UserActionResult update(@PathVariable String userId, @RequestBody ChangeUserPasswordRequest request) {
        return userActionsService.changeUserPassword(userId, request);
    }

}
