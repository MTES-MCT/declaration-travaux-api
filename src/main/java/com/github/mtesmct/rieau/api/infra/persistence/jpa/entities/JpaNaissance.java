package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class JpaNaissance {
    @Column(nullable = true, unique = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column(nullable = true, unique = false)
    private String commune;

    public JpaNaissance() {
    }

    public JpaNaissance(Date date, String commune) {
        this.date = date;
        this.commune = commune;
    }
}