package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;

@Entity(name = "demande")
@Data
@Builder
public class JpaDemande {
    @Id
    private String id;
    private String type;
    private String etat;
    private Date date;
}