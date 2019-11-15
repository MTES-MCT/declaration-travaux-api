package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class JpaUser {
    @Column(nullable = false)
    @NotNull
    private String id;
    @Column(nullable = false)
    @NotNull
    private String nom;
    @Column(nullable = false)
    @NotNull
    private String prenom;
    @Column(nullable = false)
    @NotNull
    private String profils;

    public JpaUser(@NotNull String id,
            @NotNull String nom,
            @NotNull String prenom,
            @NotNull String profils) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.profils = profils;
    }

    public JpaUser() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
            JpaUser jpaDeposant = (JpaUser) o;
        return Objects.equals(this.id, jpaDeposant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}