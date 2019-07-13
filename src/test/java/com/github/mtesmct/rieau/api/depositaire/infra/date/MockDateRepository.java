package com.github.mtesmct.rieau.api.depositaire.infra.date;

import java.util.Date;

import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DateRepository;

public class MockDateRepository implements DateRepository {
    private Date date;
    private DateConverter converter;

    @Override
    public Date now() {
        return this.date;
    }

    public MockDateRepository(DateConverter converter, String dateString) {
        this.converter = converter;
        this.date = this.converter.parse(dateString);
    }

    @Override
    public String nowText() {
        return this.converter.format(this.date);
    }

}