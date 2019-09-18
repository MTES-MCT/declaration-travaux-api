package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class JpaDeposant {
    @Column(nullable = false)
    @NotNull
    private String id;
    @Column(nullable = false)
    @NotNull
    @Email(regexp = Personne.EMAIL_REGEXP)
    private String email;

    public JpaDeposant(@NotNull String id,
            @NotNull @Email(regexp = Personne.EMAIL_REGEXP) String email) {
        this.id = id;
        this.email = email;
    }

    public JpaDeposant() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
            JpaDeposant jpaDeposant = (JpaDeposant) o;
        return Objects.equals(this.id, jpaDeposant.id) && Objects.equals(this.email, jpaDeposant.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.email);
    }
}