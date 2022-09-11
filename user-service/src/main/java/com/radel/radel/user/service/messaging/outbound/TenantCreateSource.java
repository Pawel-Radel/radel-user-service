package com.radel.radel.user.service.messaging.outbound;

import static com.radel.radel.user.service.messaging.outbound.SourceConfiguration.OUTPUT_BINDING_MIME_TYPE;
import static com.radel.radel.user.service.messaging.outbound.SourceConfiguration.OUTPUT_BINDING_NAME_TENANT_CREATE;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import com.radel.core.events.PawelTestEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Component
public class TenantCreateSource {

    private final StreamBridge streamBridge;

    public void sendTenantCreateEvent(PawelTestEvent pawelTestEvent) {

        log.debug("Sending tenant created event {event: {}}", pawelTestEvent);

        streamBridge.send(OUTPUT_BINDING_NAME_TENANT_CREATE, pawelTestEvent, OUTPUT_BINDING_MIME_TYPE);

        log.info("Sent tenant created event {event: {}}", pawelTestEvent);
    }
}
