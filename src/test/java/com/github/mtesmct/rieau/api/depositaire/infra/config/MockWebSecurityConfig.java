package com.github.mtesmct.rieau.api.depositaire.infra.config;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Identite;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdentiteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@TestConfiguration
public class MockWebSecurityConfig implements WebMvcConfigurer {

    @Autowired
    private IdentiteRepository identiteRepository;

    @Bean
    public UserDetailsService userDetailsService() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        Identite jeanMartin = new Identite("jean.martin", "Martin", "Jean", "jean.martin@monfai.fr");
        jeanMartin = this.identiteRepository.save(jeanMartin);
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        manager.createUser(User.withUsername(jeanMartin.getId()).password(encoder.encode(jeanMartin.getId())).roles("DEPOSITAIRE").build());
        return manager;
    }

}