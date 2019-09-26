package com.github.mtesmct.rieau.api.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true")
public class ClearDataConfig {
@Bean
public FlywayMigrationStrategy clean() {
    return flyway -> {
        flyway.clean();
        flyway.migrate();
    };
}
}