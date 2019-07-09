package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.infra.date.FakeDateRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FakeDateConfig {

    @Bean
    public DateRepository dateRepository(@Value("${date.format}") String formatDate,
            @Value("${fake.date}") String dateString) {
        return new FakeDateRepository(formatDate, dateString);
    }
}