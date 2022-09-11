package com.radel.services.user.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserException extends ResponseStatusException {

    public UserException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public static UserException userNotFound(String userId) {
        return new UserException(HttpStatus.NOT_FOUND, String.format("User not found with id:", userId));
    }
}
