package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "Statut")
@Table(name = "statuts")
@Getter
@Setter
public class JpaStatut {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, length = 14)
    @NotNull
    @Enumerated(EnumType.STRING)
    private EnumStatuts statut;
    @Column(nullable = true, unique = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebut;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "dossier_id")
    private JpaDossier dossier;

    public JpaStatut(@NotNull JpaDossier dossier, @NotNull EnumStatuts statut, Date dateDebut) {
        this(statut);
        this.dateDebut = dateDebut;
    }

    public JpaStatut(@NotNull EnumStatuts statut) {
        this.statut = statut;
    }

    public JpaStatut() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaStatut jpaStatut = (JpaStatut) o;
        return Objects.equals(this.statut, jpaStatut.statut) && Objects.equals(this.dateDebut, jpaStatut.dateDebut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.statut, this.dateDebut);
    }
}