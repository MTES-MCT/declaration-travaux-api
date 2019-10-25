package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaDossierFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaMessageFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaStatutFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

@Repository
@Slf4j
public class JpaDossierRepository implements DossierRepository {

    @Autowired
    private JpaSpringDossierRepository jpaSpringDossierRepository;
    @Autowired
    private JpaDossierFactory jpaDossierFactory;
    @Autowired
    private JpaStatutFactory jpaStatutFactory;
    @Autowired
    private JpaMessageFactory jpaMessageFactory;

    @Override
    public Optional<Dossier> findById(String id) {
        List<JpaDossier> jpaDossiers = this.jpaSpringDossierRepository.findAllByDossierId(id);
        Optional<Dossier> dossier = Optional.empty();
        if (!jpaDossiers.isEmpty()) {
            JpaDossier jpaDossier = jpaDossiers.get(0);
            try {
                dossier = Optional.ofNullable(this.jpaDossierFactory.fromJpa(jpaDossier));
            } catch (PatternSyntaxException | CommuneNotFoundException e) {
                log.error("{}", e);
            }
        }
        return dossier;
    }

    @Override
    public List<Dossier> findByDeposantId(String deposantId) {
        List<Dossier> dossiers = new ArrayList<Dossier>();
        this.jpaSpringDossierRepository.findAllByDeposantId(deposantId).forEach(jpaDossier -> {
            try {
                dossiers.add(this.jpaDossierFactory.fromJpa(jpaDossier));
            } catch (PatternSyntaxException | CommuneNotFoundException e) {
                log.error("{}", e);
            }
        });
        return dossiers;
    }

    @Override
    public Optional<Dossier> findByFichierId(String fichierId) {
        Optional<JpaDossier> jpaDossier = this.jpaSpringDossierRepository.findOneByPiecesJointesIdFichierId(fichierId);
        Optional<Dossier> dossier = Optional.empty();
        if (jpaDossier.isPresent()) {
            log.debug("cerfa?={}", jpaDossier.get().cerfa().isPresent());
            log.debug("piecesJointes={}", jpaDossier.get().getPiecesJointes().stream().map(pj -> pj.getId().getCode().getNumero()).toArray());
            try {
                dossier = Optional.ofNullable(this.jpaDossierFactory.fromJpa(jpaDossier.get()));
            } catch (PatternSyntaxException | CommuneNotFoundException e) {
                log.error("{}", e);
            }
        }
        return dossier;
    }

    @Override
    public List<Dossier> findByCommune(String commune) {
        List<Dossier> dossiers = new ArrayList<Dossier>();
        this.jpaSpringDossierRepository.findAllByProjetAdresseCodePostal(commune).forEach(jpaDossier -> {
            try {
                dossiers.add(this.jpaDossierFactory.fromJpa(jpaDossier));
            } catch (PatternSyntaxException | CommuneNotFoundException e) {
                log.error("{}", e);
            }
        });
        return dossiers;
    }

    private void savePieceJointe(JpaDossier jpaDossier, PieceJointe pieceJointe) {
        if (pieceJointe.fichierId() == null)
            throw new NullPointerException("Le fichier id de la pièce jointe est nul");
        if (pieceJointe.code() == null)
            throw new NullPointerException("Le code de la pièce jointe est nul");
        if (pieceJointe.code().type() == null)
            throw new NullPointerException("Le type du code de la pièce jointe est nul");
        jpaDossier.addPieceJointe(new JpaPieceJointe(new JpaPieceJointeId(jpaDossier,
                new JpaCodePieceJointe(pieceJointe.code().type().toString(), pieceJointe.code().numero()),
                pieceJointe.fichierId().toString())));
    }

    private void saveStatut(JpaDossier jpaDossier, Statut statut) {
        jpaDossier.addStatut(this.jpaStatutFactory.toJpa(jpaDossier, statut));
    }

    private void saveMessage(JpaDossier jpaDossier, Message message) {
        jpaDossier.addMessage(this.jpaMessageFactory.toJpa(jpaDossier, message));
    }

    @Override
    public Dossier save(Dossier dossier) {
        JpaDossier jpaDossierAfter = this.jpaDossierFactory.toJpa(dossier);
        List<JpaDossier> jpaDossiersBefore = this.jpaSpringDossierRepository
                .findAllByDossierId(dossier.identity().toString());
        if (!jpaDossiersBefore.isEmpty()) {
            JpaDossier jpaDossierBefore = jpaDossiersBefore.get(0);
            jpaDossierAfter.setId(jpaDossierBefore.getId());
            jpaDossierAfter.getProjet().setId(jpaDossierBefore.getProjet().getId());
            if (!dossier.pieceJointes().isEmpty())
                dossier.pieceJointes().forEach(pieceJointe -> savePieceJointe(jpaDossierBefore, pieceJointe));
            if (!dossier.historiqueStatuts().isEmpty())
                dossier.historiqueStatuts().forEach(statut -> saveStatut(jpaDossierBefore, statut));
            if (!dossier.messages().isEmpty())
                dossier.messages().forEach(message -> saveMessage(jpaDossierBefore, message));
        }
        jpaDossierAfter = this.jpaSpringDossierRepository.save(jpaDossierAfter);
        try {
            dossier = this.jpaDossierFactory.fromJpa(jpaDossierAfter);
        } catch (PatternSyntaxException | CommuneNotFoundException e) {
            log.error("{}", e);
        }
        return dossier;
    }

    @Override
    public void delete(DossierId id) throws DossierNotFoundException {
        List<JpaDossier> jpaDossiers = this.jpaSpringDossierRepository.findAllByDossierId(id.toString());
        if (jpaDossiers.isEmpty())
            throw new DossierNotFoundException(id);
        JpaDossier jpaDossier = jpaDossiers.get(0);
        jpaDossier.removeAllChildren();
        this.jpaSpringDossierRepository.delete(jpaDossier);
    }

}