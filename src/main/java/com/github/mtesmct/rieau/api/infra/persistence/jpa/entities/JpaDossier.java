package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;

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

    /**
     * Statut minimal = DEPOSE
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "dossier", orphanRemoval = true)
    @OrderBy("dateDebut")
    @Size(min = 1)
    private Set<JpaStatut> statuts;
    @Embedded
    @NotNull
    @AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "deposant_id")),
            @AttributeOverride(name = "email", column = @Column(name = "deposant_email")) })
    private JpaUser deposant;
    @Column(nullable = false, length = 4)
    @NotNull
    @Enumerated(EnumType.STRING)
    private EnumTypes type;

    /**
     * Piece jointe minimale = CERFA
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "id.dossier", orphanRemoval = true)
    @OrderBy("createdOn")
    @Size(min = 1)
    private Set<JpaPieceJointe> piecesJointes;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "dossier", orphanRemoval = true)
    @OrderBy("date")
    private Set<JpaMessage> messages;

    public void addPieceJointe(JpaPieceJointe pieceJointe) {
        pieceJointe.getId().setDossier(this);
        this.piecesJointes.add(pieceJointe);
    }
    public void addStatut(JpaStatut statut) {
        statut.setDossier(this);
        this.statuts.add(statut);
    }
    public void addMessage(JpaMessage message) {
        message.setDossier(this);
        this.messages.add(message);
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

    public JpaDossier(@NotNull String dossierId,
            @NotNull JpaUser deposant, @NotNull EnumTypes type) {
        this();
        this.dossierId = dossierId;
        this.deposant = deposant;
        this.type = type;
    }

    public JpaDossier() {
        this.piecesJointes = new LinkedHashSet<JpaPieceJointe>();
        this.statuts = new LinkedHashSet<JpaStatut>();
        this.messages = new LinkedHashSet<JpaMessage>();
    }

    @Transient
    public Optional<JpaPieceJointe> cerfa(){
        return this.piecesJointes.stream().filter(pieceJointe -> pieceJointe.isCerfa()).findAny();
    }

    @Transient
    public Optional<JpaPieceJointe> decision(){
        return this.piecesJointes.stream().filter(pieceJointe -> pieceJointe.isDecision()).findAny();
    }
}