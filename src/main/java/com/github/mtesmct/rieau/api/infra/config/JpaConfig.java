package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.infra.PackageScan;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories.JpaNaturalRepository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {PackageScan.class}, repositoryBaseClass = JpaNaturalRepository.class)
public class JpaConfig {

}