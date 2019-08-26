package com.github.mtesmct.rieau.api.depositaire.infra.config;

import com.github.mtesmct.rieau.api.depositaire.infra.PackageScan;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.repositories.JpaNaturalRepository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {PackageScan.class}, repositoryBaseClass = JpaNaturalRepository.class)
public class JpaConfig {

}