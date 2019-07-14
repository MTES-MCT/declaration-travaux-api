package com.github.mtesmct.rieau.api.depositaire.infra.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("staging")
public class ClearDataConfig {
@Bean
public FlywayMigrationStrategy clean() {
    return flyway -> {
        flyway.clean();
        flyway.migrate();
    };
}
}