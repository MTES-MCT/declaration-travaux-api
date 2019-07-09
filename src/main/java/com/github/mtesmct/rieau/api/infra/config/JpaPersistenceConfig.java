package com.github.mtesmct.rieau.api.infra.config;
import com.github.mtesmct.rieau.api.domain.entities.Identite;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories.JpaIdentiteRepository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {JpaIdentiteRepository.class})
@ComponentScan(basePackageClasses = {JpaIdentiteRepository.class})
@EntityScan(basePackageClasses = {Identite.class})
public class JpaPersistenceConfig {
}