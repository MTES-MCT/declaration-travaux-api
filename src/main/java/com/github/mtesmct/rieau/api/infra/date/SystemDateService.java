package com.github.mtesmct.rieau.api.infra.date;

import java.util.Date;

import com.github.mtesmct.rieau.api.domain.services.DateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SystemDateService implements DateService {

    private Date date;
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;
    @Autowired
    @Qualifier("yearConverter")
    private DateConverter yearConverter;

    @Override
    public Date now() {
        return this.date;
    }

    public SystemDateService() {
        this.date = new Date();
    }

    @Override
    public String nowText() {
        return this.dateTimeConverter.format(this.date);
    }

    @Override
    public String year() {
        return this.yearConverter.format(this.date);
    }

}