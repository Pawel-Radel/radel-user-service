package com.radel.radel.user.service.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.keycloak.events.admin.OperationType;

import com.radel.services.user.api.User;

public class UserHelper {

    public static void updateGroup(User user, String groupId, OperationType operationType) {
        List<String> userGroups = user.getGroups();

        switch (operationType) {
            case CREATE: {
                if (!userGroups.contains(groupId)) {
                    userGroups.add(groupId);
                }
            }
            break;
            case DELETE:
                userGroups.remove(groupId);
                break;
        }

        user.setGroups(userGroups);
    }

    public static void updateRoles(User user, List<String> roles, OperationType operationType) {
        List<String> userRoles = user.getRoles();

        switch (operationType) {
            case CREATE:
                userRoles = mergeWithoutDuplicates(userRoles, roles);
                break;
            case DELETE:
                userRoles.removeAll(roles);
                break;
        }

        user.setRoles(userRoles);
    }

    private static List<String> mergeWithoutDuplicates(List<String> list1, List<String> list2) {
        Set<String> set = new HashSet<>(list1);
        set.addAll(list2);
        return new ArrayList<>(set);
    }
}
