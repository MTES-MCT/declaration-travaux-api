package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.application.auth.Role;
import com.github.mtesmct.rieau.api.infra.http.DossiersController;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@TestConfiguration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "false")
public class MockWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests()            
            .anyRequest().authenticated().antMatchers(DossiersController.ROOT_URL+"*")
            .hasRole(Role.DEPOSANT.toString())
            .antMatchers(HttpMethod.POST, DossiersController.ROOT_URL)
            .hasRole(Role.BETA.toString()).and().formLogin().loginPage("/sso/login");
    }
}