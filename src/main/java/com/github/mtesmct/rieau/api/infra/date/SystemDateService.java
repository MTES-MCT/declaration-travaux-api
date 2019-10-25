package com.github.mtesmct.rieau.api.infra.date;

import com.github.mtesmct.rieau.api.domain.services.DateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@ConditionalOnMissingBean(name = {"mockDateService"})
public class SystemDateService implements DateService {

    @Autowired
    private DateConverter<LocalDateTime> localDateTimeConverter;

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    @Override
    public String nowText() {
        return this.localDateTimeConverter.formatDateTime(now());
    }

    @Override
    public String year() {
        return this.localDateTimeConverter.formatYear(now());
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