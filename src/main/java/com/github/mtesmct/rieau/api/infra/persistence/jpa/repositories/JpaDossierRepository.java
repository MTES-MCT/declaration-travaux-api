package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaDossierFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaProjetFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class JpaDossierRepository implements DossierRepository {

    @Autowired
    private JpaSpringDossierRepository jpaSpringDossierRepository;
    @Autowired
    private JpaDossierFactory jpaDossierFactory;
    @Autowired
    private JpaProjetFactory jpaProjetFactory;
    @Autowired
    private JpaProjetRepository jpaProjetRepository;

    @Override
    public Optional<Dossier> findById(String id) {
        Optional<JpaDossier> jpaDossier = this.jpaSpringDossierRepository.findBySimpleNaturalId(id);
        Optional<Dossier> dossier = Optional.empty();
        if (jpaDossier.isPresent()) {
            Optional<JpaProjet> jpaProjet = this.jpaProjetRepository.findById(jpaDossier.get().getId());
            if (jpaProjet.isEmpty())
                throw new NullPointerException("Le projet du dossier ne peut pas être nul.");
            try {
                dossier = Optional.ofNullable(this.jpaDossierFactory.fromJpa(jpaDossier.get(), jpaProjet.get()));
            } catch (PatternSyntaxException | CommuneNotFoundException e) {
                log.debug("{}", e);
            }
        }
        return dossier;
    }

    private JpaProjet findJpaProjet(JpaDossier jpaDossier) {
        Optional<JpaProjet> jpaProjet = this.jpaProjetRepository.findById(jpaDossier.getId());
        if (jpaProjet.isEmpty())
            throw new NullPointerException("Le projet du dossier ne peut pas être nul.");
        return jpaProjet.get();
    }

    @Override
    public List<Dossier> findByDeposantId(String deposantId) {
        List<Dossier> dossiers = new ArrayList<Dossier>();
        this.jpaSpringDossierRepository.findAllByDeposantId(deposantId).forEach(jpaDossier -> {
            try {
                dossiers.add(this.jpaDossierFactory.fromJpa(jpaDossier, findJpaProjet(jpaDossier)));
            } catch (PatternSyntaxException | CommuneNotFoundException e) {
                log.debug("{}", e);
            }
        });
        return dossiers;
    }

    @Override
    public List<Dossier> findByCommune(String commune) {
        List<Dossier> dossiers = new ArrayList<Dossier>();
        this.jpaSpringDossierRepository.findAllByProjetAdresseCodePostal(commune).forEach(jpaDossier -> {
            try {
                dossiers.add(this.jpaDossierFactory.fromJpa(jpaDossier, findJpaProjet(jpaDossier)));
            } catch (PatternSyntaxException | CommuneNotFoundException e) {
                log.debug("{}", e);
            }
        });
        return dossiers;
    }

    @Override
    public Dossier save(Dossier dossier) {
        JpaDossier jpaDossierAfter = this.jpaDossierFactory.toJpa(dossier);
        JpaProjet jpaProjetAfter = this.jpaProjetFactory.toJpa(jpaDossierAfter, dossier.projet());
        Optional<JpaDossier> jpaDossierBefore = this.jpaSpringDossierRepository
                .findBySimpleNaturalId(dossier.identity().toString());
        if (jpaDossierBefore.isPresent()) {
            jpaDossierAfter.setId(jpaDossierBefore.get().getId());
            jpaProjetAfter.setDossier(jpaDossierAfter);
            if (!dossier.pieceJointes().isEmpty())
                dossier.pieceJointes().forEach(pieceJointe -> savePieceJointe(jpaDossierBefore.get(), pieceJointe));
        }
        jpaDossierAfter = this.jpaSpringDossierRepository.save(jpaDossierAfter);
        jpaProjetAfter.setDossier(jpaDossierAfter);
        jpaProjetAfter.setId(jpaDossierAfter.getId());
        jpaProjetAfter = this.jpaProjetRepository.save(jpaProjetAfter);
        try {
            dossier = this.jpaDossierFactory.fromJpa(jpaDossierAfter, jpaProjetAfter);
        } catch (PatternSyntaxException | CommuneNotFoundException e) {
            log.debug("{}", e);
        }
        return dossier;
    }

    @Override
    public boolean isDeposantOwner(String deposantId, String fichierId) {
        return this.jpaSpringDossierRepository.existsByDeposantIdAndPiecesJointesIdFichierId(deposantId, fichierId);
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

}