package com.github.mtesmct.rieau.api.infra.config;
import com.github.mtesmct.rieau.api.infra.http.DepositaireController;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ComponentScan(basePackageClasses = {DepositaireController.class})
public class WebConfig {
}