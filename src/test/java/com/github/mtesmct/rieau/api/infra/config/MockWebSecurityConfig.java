package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.application.auth.Role;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.infra.application.auth.MockUserService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class MockWebSecurityConfig {

    @Autowired
    private MockUserService userService;

    @Bean
    @Qualifier("deposantBeta")
    public PersonnePhysique deposantBeta(){
        return this.userService.findUserById((WithDeposantAndBetaDetails.ID)).get();
    }

    @Bean
    @Qualifier("instructeurNonBeta")
    public PersonnePhysique instructeurNonBeta(){
        return this.userService.findUserById(WithInstructeurNonBetaDetails.ID).get();
    }

    @Bean
    public UserDetailsService userDetailsService() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        manager.createUser(User.withUsername(WithDeposantAndBetaDetails.ID).password(encoder.encode(WithDeposantAndBetaDetails.ID)).roles(Role.DEMANDEUR.toString(), Role.BETA.toString()).build());
        manager.createUser(User.withUsername(WithInstructeurNonBetaDetails.ID).password(encoder.encode(WithInstructeurNonBetaDetails.ID)).roles(Role.INSTRUCTEUR.toString()).build());
        return manager;
    }

}