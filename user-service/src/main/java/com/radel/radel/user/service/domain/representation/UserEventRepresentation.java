package com.radel.radel.user.service.domain.representation;

import static com.radel.radel.user.service.mapper.UserMapper.toOutputAttributes;
import static com.radel.services.user.api.UserEditableFieldEnum.ATTRIBUTES;
import static com.radel.services.user.api.UserEditableFieldEnum.EMAIL;
import static com.radel.services.user.api.UserEditableFieldEnum.EMAIL_VERIFIED;
import static com.radel.services.user.api.UserEditableFieldEnum.NAME;
import static com.radel.services.user.api.UserEditableFieldEnum.SURNAME;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.radel.services.user.api.User;
import com.radel.services.user.api.UserEditableFieldEnum;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEventRepresentation {

    private String id;

    private String firstName;

    private String lastName;

    private Map<String, List<String>> attributes;

    private String email;

    private Boolean emailVerified;

    private Boolean enabled;

    public static List<UserEditableFieldEnum> getFields() {
        return asList(NAME, SURNAME, ATTRIBUTES, EMAIL, EMAIL_VERIFIED);
    }

    public User toUser() {
        return User.builder()
                .userId(id)
                .email(email)
                .name(firstName)
                .surname(lastName)
                .emailVerified(emailVerified)
                .enabled(enabled)
                .attributes(toOutputAttributes(attributes))
                .roles(emptyList())
                .groups(emptyList())
                .build();
    }
}
