package com.github.mtesmct.rieau.api.infra.date;

import java.util.Date;

import com.github.mtesmct.rieau.api.domain.services.DateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

@TestComponent
@ConditionalOnProperty(value = "app.datetime.mock", matchIfMissing = true, havingValue = "none")
@Primary
public class MockDateService implements DateService {
    
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter converter;

    @Value("${app.datetime.mock}")
    private String dateString;

    @Override
    public Date now() {
        return this.converter.parse(dateString);
    }

    @Override
    public String nowText() {
        return this.dateString;
    }

}