package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;

import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "PersonnePhysique")
@Table(name = "personnes_physiques")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JpaPersonnePhysique {
    @Id
    @GeneratedValue
    private Long id;
    @NaturalId
    @Column(nullable = false, unique = true)
    private String personnePhysiqueId;
    @Email(regexp = PersonnePhysique.EMAIL_REGEXP)
    @Column(nullable = false, unique = true)
    private String email;
    @Enumerated
    private Sexe sexe;
    @Column(nullable = true, unique = false)
    private String nom;
    @Column(nullable = true, unique = false)
    private String prenom;
    @Column(nullable = true, unique = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateNaissance;
    @Column(nullable = true, unique = false)
    private String communeNaissance;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaPersonnePhysique jpaPersonnePhysique = (JpaPersonnePhysique) o;
        return Objects.equals(personnePhysiqueId, jpaPersonnePhysique.personnePhysiqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personnePhysiqueId);
    }
}