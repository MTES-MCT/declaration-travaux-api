package com.github.mtesmct.rieau.rieauapi.infra.repositories.fakedate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.mtesmct.rieau.rieauapi.domain.repositories.DateRepository;

import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class FakeDateRepository implements DateRepository {

    private Date date;
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private SimpleDateFormat formatter;

    @Override
    public Date now() {
        return this.date;
    }

    public FakeDateRepository() {
        this.date = new Date();
    }

    public FakeDateRepository(String date) {
        this.formatter = new SimpleDateFormat(DATE_FORMAT);
        try {
            this.date = this.formatter.parse(date);
        } catch (ParseException e) {
            log.error("format de date " + date + "non reconnu. Attendu: " + DATE_FORMAT, e);
        }
    }

}