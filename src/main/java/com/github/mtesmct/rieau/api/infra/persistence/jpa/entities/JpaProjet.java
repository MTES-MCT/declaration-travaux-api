package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalIdCache;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "Projet")
@Table(name = "PROJETS")
@Getter
@Setter
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JpaProjet {
    @Id
    private Long id;
    @Embedded
    @NotNull
    @AttributeOverrides({
            @AttributeOverride(name = "construction_nouvelle", column = @Column(name = "nature_construction_nouvelle")) })
    private JpaNature nature;
    @Embedded
    @NotNull
    @AttributeOverrides({ @AttributeOverride(name = "voie", column = @Column(name = "adresse_voie")),
            @AttributeOverride(name = "numero", column = @Column(name = "adresse_numero")),
            @AttributeOverride(name = "lieu_dit", column = @Column(name = "adresse_lieu_dit")),
            @AttributeOverride(name = "code_postal", column = @Column(name = "adresse_code_postal")),
            @AttributeOverride(name = "bp", column = @Column(name = "adresse_bp")),
            @AttributeOverride(name = "cedex", column = @Column(name = "adresse_cedex")) })
    private JpaAdresse adresse;
    @Column(name = "parcelles_cadastrales", nullable = false)
    @NotBlank
    private String parcelles;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private JpaDossier dossier;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaProjet jpaProjet = (JpaProjet) o;
        return Objects.equals(this.id, jpaProjet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public JpaProjet(@NotNull JpaDossier dossier, @NotNull JpaNature nature, @NotNull JpaAdresse adresse,
            @NotBlank String parcelles) {
        this.dossier = dossier;
        this.nature = nature;
        this.adresse = adresse;
        this.parcelles = parcelles;
    }

    public JpaProjet() {
    }

    @Override
    public String toString() {
        return "JpaProjet [adresse=" + adresse + ", dossier=" + dossier + ", id=" + id + ", nature=" + nature
                + ", parcelles=" + parcelles + "]";
    }

}