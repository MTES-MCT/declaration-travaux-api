package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.application.auth.Role;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class MockWebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        manager.createUser(User.withUsername("jean.martin").password(encoder.encode("jean.martin")).roles(Role.DEMANDEUR.toString(), Role.BETA.toString()).build());
        manager.createUser(User.withUsername("jacques.dupont").password(encoder.encode("jacques.dupont")).roles(Role.INSTRUCTEUR.toString()).build());
        return manager;
    }

}