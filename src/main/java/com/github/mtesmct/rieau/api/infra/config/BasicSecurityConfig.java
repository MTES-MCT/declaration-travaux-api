package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.application.auth.MockUsers;
import com.github.mtesmct.rieau.api.application.auth.Roles;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "keycloak", name = "enabled", havingValue = "false")
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(MockUsers.JEAN_MARTIN).password(MockUsers.JEAN_MARTIN)
                .roles(Roles.DEPOSANT, Roles.BETA).and().withUser(MockUsers.CLAIRE_DENIS)
                .password(MockUsers.CLAIRE_DENIS).roles(Roles.DEPOSANT, Roles.BETA).and().withUser(MockUsers.JACQUES_DUPONT)
                .password(MockUsers.JACQUES_DUPONT).roles(Roles.INSTRUCTEUR).and().withUser(MockUsers.MADAME_LE_MAIRE)
                .password(MockUsers.MADAME_LE_MAIRE).roles(Roles.MAIRIE, Roles.BETA);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.apply(new CommonHttpConfigurer());
    }

}