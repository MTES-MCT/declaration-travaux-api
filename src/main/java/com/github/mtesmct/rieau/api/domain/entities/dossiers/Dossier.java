package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Date;

import com.github.mtesmct.rieau.api.domain.entities.Entity;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;

public class Dossier implements Entity<Dossier, DossierId> {
    private DossierId id;
    public StatutDossier statut;
    public TypeDossier type;
    private Date dateDepot;
    private PersonnePhysique deposant;
    private Petitionnaires petitionnaires;
    private Projet projet;
    private PieceJointe cerfa;
    private PiecesJointesObligatoires piecesJointesObligatoires;
    private PiecesJointesOptionnelles piecesJointesOptionnelles;

    public Date dateDepot(){
        return this.dateDepot;
    }
    public Projet projet(){
        return this.projet;
    }
    public StatutDossier statut(){
        return this.statut;
    }
    public Petitionnaires petitionnaires(){
        return this.petitionnaires;
    }
    public PersonnePhysique deposant(){
        return this.deposant;
    }
    public PieceJointe cerfa(){
        return this.cerfa;
    }
    public TypeDossier type(){
        return this.type;
    }

    public PiecesJointesObligatoires piecesJointesObligatoires() {
        return this.piecesJointesObligatoires;
    }

    public PiecesJointesOptionnelles piecesJointesOptionnelles() {
        return this.piecesJointesOptionnelles;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final Dossier other = (Dossier) object;
        return this.hasSameIdentityAs(other);
    }

    @Override
    public String toString() {
        return this.id.toString();
    }

    @Override
    public DossierId identity() {
        return this.id;
    }

    @Override
    public boolean hasSameIdentityAs(Dossier other) {
        return other != null && this.id.hasSameValuesAs(other.id);
    }

    public Dossier(DossierId id, PersonnePhysique deposant, Date dateDepot, PieceJointe cerfa, TypeDossier type) {
        if (id == null)
            throw new NullPointerException("L'id du dépôt ne peut être nul");
        this.id = id;
        if (deposant == null)
            throw new NullPointerException("Le deposant ne peut être nul");
        this.deposant = deposant;
        this.statut = StatutDossier.DEPOSE;
        if (dateDepot == null)
            throw new NullPointerException("La date du dépôt ne peut être nulle");
        this.dateDepot = dateDepot;
        if (cerfa != null && !cerfa.isCerfa())
            throw new IllegalArgumentException("La piece jointe n'est pas un CERFA valide");
        this.cerfa = cerfa;
        if (cerfa != null && type == null)
            throw new NullPointerException("Le type de dossier ne peut pas être nul");
        this.type = type;
    }

}