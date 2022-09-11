package com.radel.services.user.api;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String userId;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private String email;

    @Null
    private Long createdTimestamp;

    @NotNull
    private Boolean enabled;

    @NotNull
    private Boolean emailVerified;

    private Map<String, String> attributes;

    @NotNull
    private List<String> roles;

    private List<String> groups;
}
