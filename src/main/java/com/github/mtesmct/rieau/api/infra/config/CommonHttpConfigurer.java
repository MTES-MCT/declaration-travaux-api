package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.application.auth.Roles;
import com.github.mtesmct.rieau.api.infra.http.DossiersController;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

public class CommonHttpConfigurer extends AbstractHttpConfigurer<CommonHttpConfigurer, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
                .and().authorizeRequests().antMatchers(HttpMethod.GET, DossiersController.ROOT_URI + "/**")
                .hasRole(Roles.DEPOSANT).antMatchers(HttpMethod.POST, DossiersController.ROOT_URI + "/**")
                .hasRole(Roles.BETA).anyRequest().denyAll();
    }
}