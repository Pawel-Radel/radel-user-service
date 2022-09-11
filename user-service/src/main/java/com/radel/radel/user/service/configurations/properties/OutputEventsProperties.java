package com.radel.radel.user.service.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "events.output")
//@Configuration
public class OutputEventsProperties {

    private boolean enabled = false;
}
