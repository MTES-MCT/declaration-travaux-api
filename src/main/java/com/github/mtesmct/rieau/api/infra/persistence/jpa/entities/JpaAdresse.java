package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class JpaAdresse {
    @Column
    private String numero;
    @Column
    private String voie;
    @Column
    private String lieuDit;
    @Column
    private String bp;
    @Column
    private String cedex;
    @Column(nullable = false)
    @NotNull
    private String codePostal;

    public JpaAdresse(String numero, String voie, String lieuDit, @NotNull String codePostal, String bp, String cedex) {
        this(codePostal);
        this.numero = numero;
        this.voie = voie;
        this.lieuDit = lieuDit;
        this.bp = bp;
        this.cedex = cedex;
    }

    public JpaAdresse(@NotNull String codePostal) {
        this.codePostal = codePostal;
    }

    public JpaAdresse() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaAdresse jpaNature = (JpaAdresse) o;
        return Objects.equals(this.numero, jpaNature.numero) && Objects.equals(this.voie, jpaNature.voie)
                && Objects.equals(this.lieuDit, jpaNature.lieuDit)
                && Objects.equals(this.codePostal, jpaNature.codePostal) && Objects.equals(this.bp, jpaNature.bp)
                && Objects.equals(this.cedex, jpaNature.cedex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.numero, this.voie, this.lieuDit, this.codePostal, this.bp, this.cedex);
    }
}