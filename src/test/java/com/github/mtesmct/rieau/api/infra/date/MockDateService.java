package com.github.mtesmct.rieau.api.infra.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.services.DateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestComponent;

import static java.time.temporal.ChronoUnit.DAYS;

@TestComponent
@ConditionalOnProperty(value = { "app.datetime.mock", "app.year.mock" })
public class MockDateService implements DateService {

    @Autowired
    private DateConverter<LocalDateTime> localDateTimeConverter;

    @Value("${app.datetime.mock}")
    private String dateString;
    @Value("${app.year.mock}")
    private String yearString;


    @Override
    public LocalDateTime now() {
        Optional<LocalDateTime> parsed = this.localDateTimeConverter.parse(this.dateString);
        return parsed.isEmpty() ? LocalDateTime.now() : parsed.get();
    }

    @Override
    public String nowText() {
        return this.dateString;
    }

    @Override
    public String year() {
        return this.yearString;
    }

    @Override
    public LocalDateTime parse(String texte){
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<LocalDateTime> parsedDateTime = this.localDateTimeConverter.parse(texte);
        if (parsedDateTime.isPresent())
            localDateTime = parsedDateTime.get();
        return localDateTime;
    }

    @Override
    public Integer daysUntilNow(LocalDateTime start) {
        return Long.valueOf(DAYS.between(now(), start)).intValue();
    }

}