package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "Dossier")
@Table(name = "dossiers")
@Getter
@Setter
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JpaDossier {
    @Id
    @GeneratedValue
    private Long id;
    @NaturalId
    @Column(nullable = false, unique = true)
    @NotNull
    private String dossierId;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 11)
    private StatutDossier statut;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDepot;
    @Embedded
    @NotNull
    @AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "deposant_id")),
            @AttributeOverride(name = "email", column = @Column(name = "deposant_email")) })
    private JpaDeposant deposant;
    @Column(nullable = false, length = 4)
    @NotNull
    @Enumerated(EnumType.STRING)
    private TypesDossier type;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "id.dossier", orphanRemoval = true)
    @OrderBy("createdOn")
    private Set<JpaPieceJointe> piecesJointes;

    public void addPieceJointe(JpaPieceJointe pieceJointe) {
        pieceJointe.getId().setDossier(this);
        this.piecesJointes.add(pieceJointe);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaDossier jpaDossier = (JpaDossier) o;
        return Objects.equals(this.dossierId, jpaDossier.dossierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dossierId);
    }

    public JpaDossier(@NotNull String dossierId, @NotNull StatutDossier statut, Date dateDepot,
            @NotNull JpaDeposant deposant, @NotNull TypesDossier type) {
        this();
        this.dossierId = dossierId;
        this.statut = statut;
        this.dateDepot = dateDepot;
        this.deposant = deposant;
        this.type = type;
    }

    public JpaDossier() {
        this.piecesJointes = new LinkedHashSet<JpaPieceJointe>();
    }
}