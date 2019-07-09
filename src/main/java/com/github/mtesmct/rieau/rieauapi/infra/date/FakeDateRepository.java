package com.github.mtesmct.rieau.rieauapi.infra.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.mtesmct.rieau.rieauapi.domain.repositories.DateRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeDateRepository implements DateRepository {
    private Date date;
    private SimpleDateFormat formatter;

    @Override
    public Date now() {
        return this.date;
    }

    public FakeDateRepository(String formatDate, String dateString) {
        log.debug("date.format: "+formatDate);
        this.formatter = new SimpleDateFormat(formatDate);
        try {
            this.date = this.formatter.parse(dateString);
        } catch (ParseException e) {
            log.error("Le format de date '" + dateString + "' est incorrect. Le format attendu est: '" + formatDate + "'", e);
        }
    }

}