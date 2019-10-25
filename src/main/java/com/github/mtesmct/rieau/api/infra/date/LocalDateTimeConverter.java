package com.github.mtesmct.rieau.api.infra.date;

import com.github.mtesmct.rieau.api.infra.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
public class LocalDateTimeConverter implements DateConverter<LocalDateTime> {

    @Autowired
    private AppProperties properties;

    @Override
    public String formatDateTime(LocalDateTime date) {
        String formatted = "";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.properties.getDatetimeFormat(), Locale.FRANCE);
        try {
            formatted = dateTimeFormatter.format(date);
        } catch (DateTimeException e) {
            log.error("{}",e);
        }
        return formatted;
    }

    @Override
    public String formatDate(LocalDateTime date) {
        String formatted = "";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.properties.getDateFormat(), Locale.FRANCE);
        try {
            formatted = dateTimeFormatter.format(date);
        } catch (DateTimeException e) {
            log.error("{}",e);
        }
        return formatted;
    }

    @Override
    public String formatYear(LocalDateTime date) {
        String formatted = "";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.properties.getYearFormat(), Locale.FRANCE);
        try {
            formatted = dateTimeFormatter.format(date);
        } catch (DateTimeException e) {
            log.error("{}",e);
        }
        return formatted;
    }

    @Override
    public Optional<LocalDateTime> parse(String date) {
        Optional<LocalDateTime> parsed = Optional.empty();
        log.debug("format={}", this.properties.getAllDatetimeFormat());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.properties.getAllDatetimeFormat(), Locale.FRANCE);
        TemporalAccessor temporalAccessor = dateTimeFormatter.parseBest(date, LocalDateTime::from, LocalDate::from);
        if (temporalAccessor instanceof  LocalDate)
            parsed = Optional.ofNullable(((LocalDate) temporalAccessor).atStartOfDay());
        if (temporalAccessor instanceof  LocalDateTime)
            parsed = Optional.ofNullable(((LocalDateTime) temporalAccessor));
        return parsed;
    }
}