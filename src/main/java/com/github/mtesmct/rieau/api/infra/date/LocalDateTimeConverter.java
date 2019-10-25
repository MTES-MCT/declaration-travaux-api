package com.github.mtesmct.rieau.api.infra.date;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeConverter implements DateConverter<LocalDateTime> {
    private DateTimeFormatter dateTimeFormatter;

    public DateTimeConverter(String format) {
        log.trace("Le format utilisé pour la conversion des dates est {}", format);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(format, Locale.FRANCE);
    }

    @Override
    public String format(LocalDateTime date) {
        String formatted = "";
        try {
            formatted = this.dateTimeFormatter.format(date);
        } catch (DateTimeException e) {
            log.error("{}",e);
        }
        return formatted;
    }

    @Override
    public Optional<LocalDateTime> parse(String date) {
        Optional<LocalDateTime> parsed = Optional.empty();
        try {
            parsed = Optional.ofNullable(LocalDateTime.parse(date, this.dateTimeFormatter));
        } catch (DateTimeException e) {
            log.error("{}",e);
        }
        return parsed;
    }
}