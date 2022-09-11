package com.radel.services.user.api;

import static java.lang.String.format;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserActionResult {

    private boolean success;

    private User user;

    private String errorReason;

    public static UserActionResult userDoesNotExist(String userId) {
        return UserActionResult.builder()
                .success(false)
                .user(null)
                .errorReason(format("User with id %s does not exists", userId))
                .build();
    }

}
