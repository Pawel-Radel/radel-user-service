package com.radel.radel.user.service.domain.model;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "users")
@Builder
public class UserEntity {

    @Id
    private String id;

    @Indexed(unique=true)
    private String userId;

    private String name;

    private String surname;

    private String email;

    private Long createdTimestamp;

    private Boolean enabled;

    private Boolean emailVerified;

    private Map<String, String> attributes;

    private List<String> roles;

    private List<String> groups;
}
