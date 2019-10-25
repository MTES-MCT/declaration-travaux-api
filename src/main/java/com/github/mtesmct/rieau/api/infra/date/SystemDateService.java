package com.github.mtesmct.rieau.api.infra.date;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.github.mtesmct.rieau.api.domain.services.DateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@ConditionalOnMissingBean(name = {"mockDateService"})
public class SystemDateService implements DateService {

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;
    @Autowired
    @Qualifier("dateConverter")
    private DateConverter dateConverter;
    @Autowired
    @Qualifier("yearConverter")
    private DateConverter yearConverter;

    @Override
    public Date now() {
        return new Date(System.nanoTime());
    }

    @Override
    public String nowText() {
        return this.dateTimeConverter.format(now());
    }

    @Override
    public String year() {
        return this.yearConverter.format(now());
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