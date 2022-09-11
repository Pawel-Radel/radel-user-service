package com.radel.radel.user.service.utils;

import static java.util.Arrays.asList;

import java.util.List;

import org.keycloak.events.admin.AdminEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radel.radel.user.service.domain.representation.GroupMembershipRepresentation;
import com.radel.radel.user.service.domain.representation.RealmRoleMappingRepresentation;
import com.radel.radel.user.service.domain.representation.UserEventRepresentation;
import com.radel.services.user.error.exception.RepresentationParseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RepresentationParser {

    public static UserEventRepresentation parseUserEventRepresentation(AdminEvent adminEvent) {
        try {
            return new ObjectMapper().readValue(adminEvent.getRepresentation(), UserEventRepresentation.class);
        } catch (JsonProcessingException e) {
            log.debug(e.getMessage(), e);
            throw new RepresentationParseException(e.getMessage());
        }
    }

    public static GroupMembershipRepresentation parseGroupMembershipRepresentation(AdminEvent adminEvent) {
        try {
            return new ObjectMapper().readValue(adminEvent.getRepresentation(), GroupMembershipRepresentation.class);
        } catch (JsonProcessingException e) {
            log.debug(e.getMessage(), e);
            throw new RepresentationParseException(e.getMessage());
        }
    }

    public static List<RealmRoleMappingRepresentation> parseRealmRoleMappingRepresentation(AdminEvent adminEvent) {
        try {
            RealmRoleMappingRepresentation[] representations = new ObjectMapper().readValue(adminEvent.getRepresentation(), RealmRoleMappingRepresentation[].class);

            return asList(representations);
        } catch (JsonProcessingException e) {
            log.debug(e.getMessage(), e);
            throw new RepresentationParseException(e.getMessage());
        }
    }
}
