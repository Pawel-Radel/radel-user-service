package com.radel.services.user.error.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.server.ResponseStatusException;

public class RepresentationParseException extends ResponseStatusException {

    public RepresentationParseException( String reason) {
        super(BAD_REQUEST, reason);
    }
}
