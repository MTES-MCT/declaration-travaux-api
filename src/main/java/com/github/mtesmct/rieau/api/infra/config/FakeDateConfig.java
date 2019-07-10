package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.date.FakeDateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class FakeDateConfig {

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;

    @Bean
    public DateRepository dateRepository(@Value("${fake.date}") String dateString) {
        return new FakeDateRepository(this.dateTimeConverter, dateString);
    }
}