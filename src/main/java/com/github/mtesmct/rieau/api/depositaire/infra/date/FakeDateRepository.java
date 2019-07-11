package com.github.mtesmct.rieau.api.depositaire.infra.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DateRepository;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeDateRepository implements DateRepository {
    private Date date;
    private DateConverter converter;

    @Override
    public Date now() {
        return this.date;
    }

    public FakeDateRepository(DateConverter converter, String dateString) {
        this.converter = converter;
        this.date = this.converter.parse(dateString);
    }

    @Override
    public String nowText() {
        return this.converter.format(this.date);
    }

}