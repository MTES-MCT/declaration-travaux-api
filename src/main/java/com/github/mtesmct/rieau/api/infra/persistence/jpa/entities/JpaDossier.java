package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity(name = "Dossier")
@Table(name = "dossiers")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JpaDossier {
    @Id
    @GeneratedValue
    private Long id;
    @NaturalId
    @Column(nullable = false, unique = true)
    private String dossierId;
    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 11)
    private StatutDossier statut;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column(nullable = false)
    private String deposantId;
    @Column(nullable = false)
    @Email(regexp = Personne.EMAIL_REGEXP)
    private String deposantEmail;
    @Column(nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    private TypeDossier type;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaDossier jpaDossier = (JpaDossier) o;
        return Objects.equals(dossierId, jpaDossier.dossierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dossierId);
    }
}