package com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities;

import java.sql.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Etat;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;

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

@Entity(name = "depot")
@Table(name = "depot")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JpaDepot {
    @Id
    @GeneratedValue
    private Long id;
    @NaturalId
    @Column(nullable = false, unique = true)
    private String idDepot;
    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private Type type;
    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(length = 11)
    private Etat etat;
    private Date date;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaDepot jpaDepot = (JpaDepot) o;
        return Objects.equals(idDepot, jpaDepot.idDepot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDepot);
    }
}