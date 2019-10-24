package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

@Component
@Slf4j
public class JpaDossierFactory {
    @Autowired
    private TypeDossierRepository typeDossierRepository;
    @Autowired
    private JpaPieceJointeFactory jpaPieceJointeFactory;
    @Autowired
    private JpaProjetFactory jpaProjetFactory;
    @Autowired
    private JpaStatutFactory jpaStatutFactory;
    @Autowired
    private JpaMessageFactory jpaMessageFactory;

    public JpaDossier toJpa(Dossier dossier) {
        if (dossier.deposant() == null)
            throw new NullPointerException("Le déposant ne peut pas être nul.");
        JpaUser jpaDeposant = new JpaUser(dossier.deposant().identity().toString(), dossier.deposant().email());
        JpaDossier jpaDossier = new JpaDossier(dossier.identity().toString(), jpaDeposant, dossier.type().type());
        jpaDossier.addProjet(this.jpaProjetFactory.toJpa(jpaDossier, dossier.projet()));
        if (dossier.cerfa() != null)
            jpaDossier.addPieceJointe(this.jpaPieceJointeFactory.toJpa(jpaDossier, dossier.cerfa()));
        if (!dossier.pieceJointes().isEmpty())
            dossier.pieceJointes().forEach(pieceJointe -> jpaDossier
                    .addPieceJointe(this.jpaPieceJointeFactory.toJpa(jpaDossier, pieceJointe)));
        if (!dossier.historiqueStatuts().isEmpty())
            dossier.historiqueStatuts()
                    .forEach(statut -> jpaDossier.addStatut(this.jpaStatutFactory.toJpa(jpaDossier, statut)));
        if (!dossier.messages().isEmpty())
            dossier.messages()
                    .forEach(message -> jpaDossier.addMessage(this.jpaMessageFactory.toJpa(jpaDossier, message)));
        if (dossier.decision() != null)
            jpaDossier.addPieceJointe(this.jpaPieceJointeFactory.toJpa(jpaDossier, dossier.decision()));
        return jpaDossier;
    }

    public Dossier fromJpa(JpaDossier jpaDossier)
            throws PatternSyntaxException, CommuneNotFoundException {
        if (jpaDossier.getDeposant() == null)
            throw new NullPointerException("Le déposant du dossier ne peut pas être nul.");
        Personne deposant = new Personne(jpaDossier.getDeposant().getId(), jpaDossier.getDeposant().getEmail());
        Optional<TypeDossier> type = this.typeDossierRepository.findByType(jpaDossier.getType());
        Projet projet = this.jpaProjetFactory.fromJpa(jpaDossier.getProjet());
        if (type.isEmpty())
            throw new NullPointerException("Le type de dossier ne peut pas être nul.");
        if (jpaDossier.cerfa().isEmpty())
            throw new NullPointerException("La pièce jointe CERFA ne peut pas être nulle.");
        FichierId fichierIdCerfa = new FichierId(jpaDossier.cerfa().get().getId().getFichierId());
        Dossier dossier = new Dossier(new DossierId(jpaDossier.getDossierId()), deposant, type.get(), projet,
                fichierIdCerfa);
        if (!jpaDossier.getPiecesJointes().isEmpty())
            jpaDossier.getPiecesJointes().forEach(jpaPieceJointe -> {
                try {
                    this.ajouterPieceJointe(dossier, jpaPieceJointe);
                } catch (PieceNonAJoindreException | AjouterPieceJointeException e) {
                    log.warn("Erreur {} de transformation de la piece jointe {}", e.getMessage(),
                            Objects.toString(jpaPieceJointe));
                }
            });
        if (!jpaDossier.getStatuts().isEmpty())
            jpaDossier.getStatuts().forEach(jpaStatut -> {
                try {
                    this.ajouterStatut(dossier, jpaStatut);
                } catch (StatutForbiddenException | TypeStatutNotFoundException e) {
                    log.warn("Erreur {} de transformation du statut {}", e.getMessage(), Objects.toString(jpaStatut));
                }
            });
        if (!jpaDossier.getMessages().isEmpty())
            jpaDossier.getMessages().forEach(jpaMessage -> this.ajouterMessage(dossier, jpaMessage));
        if (jpaDossier.decision().isPresent()) {
            FichierId fichierIdDecision = new FichierId(jpaDossier.decision().get().getId().getFichierId());
            dossier.ajouterDecision(fichierIdDecision);
        }
        return dossier;
    }

    private void ajouterPieceJointe(Dossier dossier, JpaPieceJointe jpaPieceJointe)
            throws PieceNonAJoindreException, AjouterPieceJointeException {
        PieceJointe pieceJointe = this.jpaPieceJointeFactory.fromJpa(dossier, jpaPieceJointe);
        if (pieceJointe != null) {
            if (!jpaPieceJointe.isCerfa() && !jpaPieceJointe.isDecision()) {
                if (pieceJointe.code() == null)
                    throw new NullPointerException("Le code de la pièce jointe ne peut pas être nul.");
                dossier.ajouterPieceJointe(pieceJointe.code().numero(), pieceJointe.fichierId());
            }
        }
    }

    private void ajouterStatut(Dossier dossier, JpaStatut jpaStatut)
            throws StatutForbiddenException, TypeStatutNotFoundException {
        Statut statut = this.jpaStatutFactory.fromJpa(jpaStatut);
        if (statut == null)
            throw new NullPointerException("Le statut ne peut pas être nul.");
        dossier.ajouterStatut(statut.dateDebut(), statut.type());
    }

    private void ajouterMessage(Dossier dossier, JpaMessage jpaMessage) {
        Message message = this.jpaMessageFactory.fromJpa(jpaMessage);
        if (message == null)
            throw new NullPointerException("Le message ne peut pas être nul.");
        dossier.ajouterMessage(message);
    }
}