package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDeposant;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JpaDossierFactory {
    @Autowired
    private TypeDossierRepository typeDossierRepository;
    @Autowired
    private JpaPieceJointeFactory jpaPieceJointeFactory;
    @Autowired
    private JpaProjetFactory jpaProjetFactory;

    public JpaDossier toJpa(Dossier dossier) {
        if (dossier.deposant() == null)
            throw new NullPointerException("Le déposant ne peut pas être nul.");
        JpaDeposant jpaDeposant = new JpaDeposant(dossier.deposant().identity().toString(), dossier.deposant().email());
        JpaDossier jpaDossier = new JpaDossier(dossier.identity().toString(), dossier.statut(), dossier.dateDepot(),
                jpaDeposant, dossier.type().type());
        if (dossier.cerfa() != null)
            jpaDossier.addPieceJointe(this.jpaPieceJointeFactory.toJpa(jpaDossier, dossier.cerfa()));
        if (!dossier.pieceJointes().isEmpty())
            dossier.pieceJointes().forEach(pieceJointe -> jpaDossier
                    .addPieceJointe(this.jpaPieceJointeFactory.toJpa(jpaDossier, pieceJointe)));
        return jpaDossier;
    }

    public Dossier fromJpa(JpaDossier jpaDossier, JpaProjet jpaProjet)
            throws PatternSyntaxException, CommuneNotFoundException {
        if (jpaDossier.getDeposant() == null)
            throw new NullPointerException("Le déposant du dossier ne peut pas être nul.");
        Personne deposant = new Personne(jpaDossier.getDeposant().getId(), jpaDossier.getDeposant().getEmail());
        Optional<TypeDossier> type = this.typeDossierRepository.findByType(jpaDossier.getType());
        if (type.isEmpty())
            throw new NullPointerException("Le type de dossier ne peut pas être nul.");
        Projet projet = this.jpaProjetFactory.fromJpa(jpaProjet);
        Dossier dossier = new Dossier(new DossierId(jpaDossier.getDossierId()), deposant, jpaDossier.getStatut(),
                jpaDossier.getDateDepot(), type.get(), projet);
        if (!jpaDossier.getPiecesJointes().isEmpty())
            jpaDossier.getPiecesJointes().forEach(jpaPieceJointe -> this.ajouterPieceJointe(dossier, jpaPieceJointe));
        return dossier;
    }

    private void ajouterPieceJointe(Dossier dossier, JpaPieceJointe jpaPieceJointe) {
        PieceJointe pieceJointe = this.jpaPieceJointeFactory.fromJpa(dossier, jpaPieceJointe);
        if (pieceJointe != null) {
            if (jpaPieceJointe.isCerfa()) {
                dossier.ajouterCerfa(pieceJointe.fichierId());
            } else {
                if (pieceJointe.code() == null)
                    throw new NullPointerException("Le code de la pièce jointe ne peut pas être nul.");
                dossier.ajouter(pieceJointe.code().numero(), pieceJointe.fichierId());
            }
        }
    }
}