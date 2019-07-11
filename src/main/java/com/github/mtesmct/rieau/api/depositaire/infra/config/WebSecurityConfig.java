package com.github.mtesmct.rieau.api.depositaire.infra.config;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Identite;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdentiteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig implements WebMvcConfigurer {

    @Autowired
    private IdentiteRepository identiteRepository;

    @Bean
    @Profile("test")
    public UserDetailsService userDetailsService() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        Identite jeanMartin = new Identite("jean.martin", "Martin", "Jean", "jean.martin@monfai.fr");
        jeanMartin = this.identiteRepository.save(jeanMartin);
        manager.createUser(User.withUsername(jeanMartin.getId()).password(jeanMartin.getId()).roles("DEPOSITAIRE").build());
        return manager;
    }

}