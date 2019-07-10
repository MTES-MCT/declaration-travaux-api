package com.github.mtesmct.rieau.api.infra.date;

import java.util.Date;

import com.github.mtesmct.rieau.api.domain.repositories.DateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("production")
public class SystemDateRepository implements DateRepository {

    private Date date;
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter converter;

    @Override
    public Date now() {
        return this.date;
    }

    public SystemDateRepository() {
        this.date = new Date();
    }

    @Override
    public String nowText() {
        return this.converter.format(this.date);
    }

}