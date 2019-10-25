package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class JpaCodePieceJointe {
    @NotNull
    @Column(nullable = true, unique = false, length = 4)
    private String type;
    @NotBlank
    @Column(nullable = true, unique = false)
    private String numero;

    public JpaCodePieceJointe() {
    }

    public JpaCodePieceJointe(String type, String numero) {
        this.type = type;
        this.numero = numero;
    }

    @Transient
    public boolean isCerfa() {
        return this.numero != null && this.numero.equals("0");
    }

    @Transient
    public boolean isDecision() {
        return this.numero != null && this.numero.equals("d");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
            JpaCodePieceJointe jpaCodePieceJointe = (JpaCodePieceJointe) o;
        return Objects.equals(this.numero, jpaCodePieceJointe.numero) && Objects.equals(this.type, jpaCodePieceJointe.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.numero, this.type);
    }

}