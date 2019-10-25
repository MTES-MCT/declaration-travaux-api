package com.github.mtesmct.rieau.api.infra.date;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.github.mtesmct.rieau.api.domain.services.DateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestComponent;

import static java.time.temporal.ChronoUnit.DAYS;

@TestComponent
@ConditionalOnProperty(value = { "app.datetime.mock", "app.year.mock" })
public class MockDateService implements DateService {

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;
    @Autowired
    @Qualifier("dateConverter")
    private DateConverter dateConverter;

    @Value("${app.datetime.mock}")
    private String dateString;
    @Value("${app.year.mock}")
    private String yearString;


    @Override
    public Date now() {
        return this.dateTimeConverter.parse(this.dateString);
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
    public Date parse(String texte){
        return this.dateConverter.parse(texte);
    }

    @Override
    public Integer daysUntilNow(Date start) {
        LocalDateTime nowLocalDateTime = now().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime dateDebutLocalDateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Long.valueOf(DAYS.between(nowLocalDateTime, dateDebutLocalDateTime)).intValue();
    }

}