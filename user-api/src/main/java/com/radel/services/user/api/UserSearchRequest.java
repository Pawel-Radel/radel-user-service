package com.radel.services.user.api;

import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class UserSearchRequest {

    Set<String> userIds;

    /**
     * true - Returns users that belong to any group specified in groups field.
     * false - Returns users that belong to all the groups specified in groups field.
     * Default value is true
     */
    Boolean belongsToAnyGroup = true;

    Set<String> groups;

    /**
     * true - Returns users that have any role specified in roles field.
     * false - Returns users that have all the roles specified in roles field.
     * Default value is true
     */
    Boolean hasAnyRole = true;

    Set<String> roles;

    /**
     * true - Returns users that have any attribute specified in attributes field.
     * false - Returns users that have all the attributes specified in attributes field.
     * Default value is true
     */
    Boolean hasAnyAttribute = true;

    Map<String, String> attributes;

    String name;

    String surname;

    String searchPhrase;

    String email;

    Boolean enabled;

    Boolean emailVerified;
}
