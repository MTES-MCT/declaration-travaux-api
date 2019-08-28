package com.github.mtesmct.rieau.api.infra.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(lazyInit = true)
public class LazyConfig {
}