package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

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
    @Column(name = "lotissement", nullable = false)
    @NotNull
    private boolean lotissement;

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
            @NotBlank String parcelles, @NotNull boolean lotissement) {
        this.dossier = dossier;
        this.nature = nature;
        this.adresse = adresse;
        this.parcelles = parcelles;
        this.lotissement = lotissement;
    }

    public JpaProjet() {
    }

    @Override
    public String toString() {
        return "JpaProjet [adresse=" + adresse + ", dossier=" + dossier + ", id=" + id + ", lotissement=" + lotissement
                + ", nature=" + nature + ", parcelles=" + parcelles + "]";
    }

}