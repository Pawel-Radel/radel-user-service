package com.radel.radel.user.service.mapper;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import com.radel.radel.user.service.domain.model.UserEntity;
import com.radel.services.user.api.User;

@Component
public class UserMapper {

    public static UserEntity toUserEntity(User user) {
        return UserEntity.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .createdTimestamp(user.getCreatedTimestamp())
                .enabled(user.getEnabled())
                .emailVerified(user.getEmailVerified())
                .attributes(user.getAttributes())
                .roles(user.getRoles())
                .groups(user.getGroups())
                .build();
    }

    public static User toUser(UserEntity entity) {
        return User.builder()
                .userId(entity.getUserId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .email(entity.getEmail())
                .createdTimestamp(entity.getCreatedTimestamp())
                .enabled(entity.getEnabled())
                .emailVerified(entity.getEmailVerified())
                .attributes(entity.getAttributes())
                .roles(entity.getRoles())
                .groups(entity.getGroups())
                .build();
    }

    public static void updateUserEntity(UserEntity source, User target) {
        source.setName(target.getName());
        source.setSurname(target.getSurname());
        source.setEmail(target.getEmail());
        source.setCreatedTimestamp(target.getCreatedTimestamp());
        source.setEnabled(target.getEnabled());
        source.setEmailVerified(target.getEmailVerified());
        source.setAttributes(target.getAttributes());
        source.setRoles(target.getRoles());
        source.setGroups(target.getGroups());
    }

    public static User toAuthUser(UserRepresentation userRepresentation, List<String> roles, List<String> groups) {
        Map<String, String> attributes = toOutputAttributes(userRepresentation.getAttributes());

        return User.builder()
                .userId(userRepresentation.getId())
                .email(userRepresentation.getEmail())
                .name(userRepresentation.getFirstName())
                .surname(userRepresentation.getLastName())
                .createdTimestamp(userRepresentation.getCreatedTimestamp())
                .enabled(userRepresentation.isEnabled())
                .emailVerified(userRepresentation.isEmailVerified())
                .attributes(attributes)
                .roles(roles)
                .groups(groups)
                .build();
    }

    public static Map<String, String> toOutputAttributes(Map<String, List<String>> attributes) {
        if (isNull(attributes)) {
            return new HashMap<>();
        }

        return attributes.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().stream().findFirst().orElse("")));
    }

    public static Map<String, List<String>> toInputAttributes(Map<String, String> attributes) {
        if (isNull(attributes)) {
            return new HashMap<>();
        }

        return attributes.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> asList(entry.getValue())));
    }

    public static List<String> toUserGroups(UserResource userResource) {

        return userResource.groups().stream().map(GroupRepresentation::getId).collect(toList());
    }

    public static List<String> toUserRoles(UserResource userResource, Set<String> managedRoles) {
        return userResource
                .roles().realmLevel()
                .listAll().stream()
                .map(RoleRepresentation::getName)
                .filter(managedRoles::contains).collect(toList());
    }
}
