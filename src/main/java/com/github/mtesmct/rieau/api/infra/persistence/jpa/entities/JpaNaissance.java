package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
public class JpaNaissance {
    @Column(nullable = true, unique = false)
    private LocalDateTime date;
    @Column(nullable = true, unique = false)
    private String commune;

    public JpaNaissance() {
    }

    public JpaNaissance(LocalDateTime date, String commune) {
        this.date = date;
        this.commune = commune;
    }
}