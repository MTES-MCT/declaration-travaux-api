package com.github.mtesmct.rieau.api.infra.config;
import com.github.mtesmct.rieau.api.infra.persistence.inmemory.InMemoryDemandeRepository;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {InMemoryDemandeRepository.class})
public class InMemoryPersistenceConfig {
}