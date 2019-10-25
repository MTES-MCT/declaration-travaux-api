package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class JpaUser {
    @Column(nullable = false)
    @NotNull
    private String id;
    @Column(nullable = false)
    @NotNull
    @Email(regexp = Personne.EMAIL_REGEXP)
    private String email;

    public JpaUser(@NotNull String id,
            @NotNull @Email(regexp = Personne.EMAIL_REGEXP) String email) {
        this.id = id;
        this.email = email;
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
        return Objects.equals(this.id, jpaDeposant.id) && Objects.equals(this.email, jpaDeposant.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.email);
    }
}