package com.radel.services.user.api;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

public enum UserSearchRequestFields {

    USER_IDS("userIds"),
    BELONGS_TO_ANY_GROUP("belongsToAnyGroup"),
    GROUPS("groups"),
    HAS_ANY_ROLE("hasAnyRole"),
    ROLES("roles"),
    HAS_ANY_ATTRIBUTE("hasAnyAttribute"),
    ATTRIBUTES("attributes"),
    NAME("name"),
    SURNAME("surname"),
    USERNAME("username"),
    SEARCH_PHRASE("searchPhrase"),
    EMAIL("email"),
    ENABLED("enabled"),
    EMAIL_VERIFIED("emailVerified");

    @Getter
    private final String value;

    UserSearchRequestFields(String value) {
        this.value = value;
    }

    public static Set<String> getValues() {
        return EnumSet.allOf(UserSearchRequestFields.class)
                .stream()
                .map(UserSearchRequestFields::getValue)
                .collect(Collectors.toSet());
    }
}
