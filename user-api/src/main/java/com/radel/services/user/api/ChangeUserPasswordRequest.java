package com.radel.services.user.api;

import lombok.Data;

@Data
public class ChangeUserPasswordRequest {
    private char[] password;
    private boolean temporary;
}
