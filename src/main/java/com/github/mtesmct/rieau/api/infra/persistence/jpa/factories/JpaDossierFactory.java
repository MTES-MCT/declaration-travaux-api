package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Statut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDeposant;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaStatut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

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

    public JpaDossier toJpa(Dossier dossier) {
        if (dossier.deposant() == null)
            throw new NullPointerException("Le déposant ne peut pas être nul.");
        JpaDeposant jpaDeposant = new JpaDeposant(dossier.deposant().identity().toString(), dossier.deposant().email());
        JpaDossier jpaDossier = new JpaDossier(dossier.identity().toString(), jpaDeposant, dossier.type().type());
        if (dossier.cerfa() != null)
            jpaDossier.addPieceJointe(this.jpaPieceJointeFactory.toJpa(jpaDossier, dossier.cerfa()));
        if (!dossier.pieceJointes().isEmpty())
            dossier.pieceJointes().forEach(pieceJointe -> jpaDossier
                    .addPieceJointe(this.jpaPieceJointeFactory.toJpa(jpaDossier, pieceJointe)));
        if (!dossier.historiqueStatuts().isEmpty())
            dossier.historiqueStatuts()
                    .forEach(statut -> jpaDossier.addStatut(this.jpaStatutFactory.toJpa(jpaDossier, statut)));
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
        return dossier;
    }

    private void ajouterPieceJointe(Dossier dossier, JpaPieceJointe jpaPieceJointe)
            throws PieceNonAJoindreException, AjouterPieceJointeException {
        PieceJointe pieceJointe = this.jpaPieceJointeFactory.fromJpa(dossier, jpaPieceJointe);
        if (pieceJointe != null) {
            if (!jpaPieceJointe.isCerfa()) {
                if (pieceJointe.code() == null)
                    throw new NullPointerException("Le code de la pièce jointe ne peut pas être nul.");
                dossier.ajouterPieceJointe(pieceJointe.code().numero(), pieceJointe.fichierId());
            }
        }
    }

    private void ajouterStatut(Dossier dossier, JpaStatut jpaStatut)
            throws StatutForbiddenException, TypeStatutNotFoundException {
        Statut statut = this.jpaStatutFactory.fromJpa(jpaStatut);
        if (statut != null) {
            dossier.ajouterStatut(statut.dateDebut(), statut.type());
        }
    }
}