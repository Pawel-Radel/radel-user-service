package com.radel.radel.user.service.configurations.properties;

import org.springframework.util.MimeType;

public class SourceConfiguration {
    public static final MimeType OUTPUT_BINDING_MIME_TYPE = MimeType.valueOf("application/+avro");
    public static final String OUTPUT_BINDING_NAME_USER_EVENTS = "user-events";
}
