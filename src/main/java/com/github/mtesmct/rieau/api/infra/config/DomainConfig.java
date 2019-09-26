package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.application.ApplicationPackageScan;
import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.domain.DomainPackageScan;
import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.services.DomainService;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackageClasses = {DomainPackageScan.class,ApplicationPackageScan.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {DomainService.class,ApplicationService.class,Factory.class}))
public class DomainConfig {

}