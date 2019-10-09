package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "Personne")
@Table(name = "personnes")
@Getter
@Setter
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JpaPersonne {
    @Id
    @GeneratedValue
    private Long id;
    @NaturalId
    @NotNull
    @Column(name = "personne_id", nullable = false, unique = true)
    private String personneId;
    @Email(regexp = Personne.EMAIL_REGEXP)
    @NotNull
    @Column(nullable = false, unique = false)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 5)
    private Sexe sexe;
    @Column(nullable = true, unique = false)
    private String nom;
    @Column(nullable = true, unique = false)
    private String prenom;
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "date", column = @Column(name = "date_naissance")),
            @AttributeOverride(name = "commune", column = @Column(name = "commune_naissance")) })
    private JpaNaissance naissance;
    @Embedded
    @NotNull
    @AttributeOverrides({ @AttributeOverride(name = "voie", column = @Column(name = "adresse_voie")),
            @AttributeOverride(name = "numero", column = @Column(name = "adresse_numero")),
            @AttributeOverride(name = "lieu_dit", column = @Column(name = "adresse_lieu_dit")),
            @AttributeOverride(name = "code_postal", column = @Column(name = "adresse_code_postal")),
            @AttributeOverride(name = "bp", column = @Column(name = "adresse_bp")),
            @AttributeOverride(name = "cedex", column = @Column(name = "adresse_cedex")) })
    private JpaAdresse adresse;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaPersonne jpaPersonne = (JpaPersonne) o;
        return Objects.equals(personneId, jpaPersonne.personneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personneId);
    }

    public JpaPersonne() {
    }

    public JpaPersonne(
        @NotNull String personneId, @Email(regexp = Personne.EMAIL_REGEXP) @NotNull String email, Sexe sexe,
            String nom, String prenom, JpaNaissance naissance, JpaAdresse adresse) {
        this(personneId, email, adresse);
        this.sexe = sexe;
        this.nom = nom;
        this.prenom = prenom;
        this.naissance = naissance;
        this.adresse = adresse;
    }

    public JpaPersonne(String personneId, @Email(regexp = Personne.EMAIL_REGEXP) @NotNull String email, @NotNull String codePostal) {
        this();
        this.personneId = personneId;
        this.email = email;
        this.adresse = new JpaAdresse(codePostal);
    }

    public JpaPersonne(String personneId, @Email(regexp = Personne.EMAIL_REGEXP) @NotNull String email, @NotNull JpaAdresse adresse) {
        this();
        this.personneId = personneId;
        this.email = email;
        this.adresse = adresse;
    }
}