package com.radel.radel.user.service.messaging.outbound;

import org.springframework.util.MimeType;

public class SourceConfiguration {

    public static final String OUTPUT_BINDING_NAME_TENANT_CREATE = "test";

    public static final MimeType OUTPUT_BINDING_MIME_TYPE = MimeType.valueOf("application/*+avro");
}
