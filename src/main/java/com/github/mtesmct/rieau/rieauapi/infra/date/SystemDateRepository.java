package com.github.mtesmct.rieau.rieauapi.infra.date;

import java.util.Date;

import com.github.mtesmct.rieau.rieauapi.domain.repositories.DateRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("production")
public class SystemDateRepository implements DateRepository {

    private Date date;

    @Override
    public Date now() {
        return this.date;
    }

    public SystemDateRepository() {
        this.date = new Date();
    }

}