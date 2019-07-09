package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;

@Entity(name = "identite")
@Data
@Builder
public class JpaIdentite {
    @Id
    private String id;
    private String nom;
    private String prenom;
    private String email;
}