package com.github.mtesmct.rieau.api.infra.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateConverter {
    private SimpleDateFormat dateFormat;
    private String format;

    public DateConverter(String format) {
        this.format = format;
        try {
            this.dateFormat = new SimpleDateFormat(format);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Erreur de création de l'objet java.text.SimpleDateFormat. Format incorrect: {}. Exception= {}",this.format, e);
        }
    }

    public String format(Date date) {
        return this.dateFormat.format(date);
    }

    public Date parse(String date) {
        Date result = null;
        try {
            result = this.dateFormat.parse(date);
        } catch (ParseException e) {
            log.error("Erreur de conversion de {} en java.util.Date. Format attendu: {}. Exception= {}", date, this.format, e);
        }
        return result;
    }
}