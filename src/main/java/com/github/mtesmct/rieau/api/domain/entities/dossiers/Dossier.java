package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.mtesmct.rieau.api.domain.entities.Entity;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

public class Dossier implements Entity<Dossier, DossierId> {
    private DossierId id;
    private List<Statut> historiqueStatuts;
    private TypeDossier type;
    private Personne deposant;
    private Projet projet;
    private PieceJointe cerfa;
    private PieceJointe decision;
    private List<PieceJointe> piecesJointes;
    private StatutComparator statutComparator;
    private List<Message> messages;

    public Projet projet() {
        return this.projet;
    }

    public List<Statut> historiqueStatuts() {
        return this.historiqueStatuts.stream().sorted(this.statutComparator).collect(Collectors.toList());
    }

    public Personne deposant() {
        return this.deposant;
    }

    public PieceJointe cerfa() {
        return this.cerfa;
    }

    public PieceJointe decision() {
        return this.decision;
    }

    public TypeDossier type() {
        return this.type;
    }

    public List<PieceJointe> pieceJointes() {
        return this.piecesJointes;
    }

    public List<Message> messages() {
        return this.messages;
    }

    public List<String> piecesAJoindre() {
        List<String> liste = new ArrayList<String>();
        if (this.projet.nature().nouvelleConstruction()) {
            if (this.type.type().equals(EnumTypes.DPMI))
                liste.add("2");
        }
        if (this.projet.localisation().lotissement()) {
            if (this.type.type().equals(EnumTypes.PCMI)) {
                liste.add("9");
                liste.add("10");
            }
        }

        return Stream.of(this.type.piecesAJoindreObligatoires(), liste).flatMap(x -> x.stream())
                .collect(Collectors.toList());
    }

    private PieceJointe ajouterCerfa(FichierId fichierId) {
        PieceJointe pieceJointe = new PieceJointe(this, new CodePieceJointe(this.type.type(), "0"), fichierId);
        this.cerfa = pieceJointe;
        return pieceJointe;
    }

    public PieceJointe ajouterDecision(FichierId fichierId) {
        PieceJointe pieceJointe = new PieceJointe(this, new CodePieceJointe(this.type.type(), "d"), fichierId);
        this.decision = pieceJointe;
        return pieceJointe;
    }

    public Optional<PieceJointe> ajouterPieceJointe(String numero, FichierId fichierId)
            throws AjouterPieceJointeException {
        Optional<PieceJointe> pieceJointe = Optional.empty();
        try {
            if (this.type == null)
                throw new AjouterPieceJointeException(new NullPointerException("Le type du dossier est nul"));
            if (numero.equals("0"))
                throw new AjouterPieceJointeException(new NumeroPieceJointeException());
            if (numero.equals("d"))
                throw new AjouterPieceJointeException(new NumeroPieceJointeException());
            pieceJointe = Optional
                    .ofNullable(new PieceJointe(this, new CodePieceJointe(this.type.type(), numero), fichierId));
            if (!pieceJointe.get().isAJoindre())
                throw new AjouterPieceJointeException(new PieceNonAJoindreException(pieceJointe.get().code()));
            this.piecesJointes.add(pieceJointe.get());
        } catch (IllegalArgumentException | NullPointerException | UnsupportedOperationException | ClassCastException
                | NoSuchElementException e) {
            throw new AjouterPieceJointeException("Ajout de la pièce jointe impossible", e);
        }
        return pieceJointe;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
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
        return "Dossier={ id={" + Objects.toString(this.id) + "}, deposant={" + Objects.toString(this.deposant)
                + "}, historiqueStatuts={" + Objects.toString(this.historiqueStatuts) + "}, type={"
                + Objects.toString(this.type) + "} }";
    }

    @Override
    public DossierId identity() {
        return this.id;
    }

    @Override
    public boolean hasSameIdentityAs(Dossier other) {
        return other != null && Objects.equals(this.id, other.id);
    }

    public Dossier(DossierId id, Personne deposant, TypeDossier type, Projet projet, FichierId fichierIdCerfa) {
        if (id == null)
            throw new NullPointerException("L'id du dépôt ne peut pas être nul");
        this.id = id;
        if (deposant == null)
            throw new NullPointerException("Le deposant ne peut pas être nul");
        this.deposant = deposant;
        if (type == null)
            throw new NullPointerException("Le type du dossier ne peut pas être nul");
        this.type = type;
        if (projet == null)
            throw new NullPointerException("Le projet du dossier ne peut pas être nul");
        this.projet = projet;
        if (fichierIdCerfa == null)
            throw new NullPointerException("Le fichier du CERFA du dossier ne peut pas être nul");
        this.cerfa = this.ajouterCerfa(fichierIdCerfa);
        this.historiqueStatuts = new ArrayList<Statut>();
        this.piecesJointes = new ArrayList<PieceJointe>();
        this.messages = new ArrayList<Message>();
        this.statutComparator = new StatutComparator();
    }

    public void ajouterStatut(Date dateDebut, TypeStatut type) throws StatutForbiddenException {
        Statut statut = new Statut(type, dateDebut);
        if (statutActuel().isEmpty() && !Objects.equals(type.identity(), EnumStatuts.DEPOSE))
            throw new StatutForbiddenException();
        if (!this.historiqueStatuts().isEmpty() && this.historiqueStatuts().contains(statut) && type.unique())
            throw new StatutForbiddenException(type.identity());
        if (statutActuel().isPresent()) {
            int statutComparedToActuel = this.statutComparator.compare(statut, statutActuel().get());
            if (statutComparedToActuel != 1)
                throw new StatutForbiddenException(type.identity(), statutActuel().get().type().identity());
        }
        this.historiqueStatuts.add(statut);
    }

    public Optional<Statut> statutActuel() {
        return this.historiqueStatuts.stream().max(this.statutComparator);
    }

    public void ajouterMessage(Message message) {
        this.messages.add(message);
    }

}