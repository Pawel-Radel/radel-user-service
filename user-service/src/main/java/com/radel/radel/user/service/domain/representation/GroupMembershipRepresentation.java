package com.radel.radel.user.service.domain.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMembershipRepresentation {

    private String id;

    private String name;

    private String path;
}
