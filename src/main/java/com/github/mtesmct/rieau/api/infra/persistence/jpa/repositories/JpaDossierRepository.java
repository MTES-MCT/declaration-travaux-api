package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Message;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Statut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.factories.PersonneParseException;
import com.github.mtesmct.rieau.api.domain.factories.UserParseException;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.SaveDossierException;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaMessage;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaStatut;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaDossierFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaMessageFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPieceJointeFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaStatutFactory;

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
    private JpaStatutFactory jpaStatutFactory;
    @Autowired
    private JpaPieceJointeFactory jpaPieceJointeFactory;
    @Autowired
    private JpaMessageFactory jpaMessageFactory;

    @Override
    public Optional<Dossier> findById(String id) {
        Optional<JpaDossier> jpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithPiecesJointes(id);
        jpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithMessages(id);
        jpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithStatuts(id);
        Optional<Dossier> dossier = Optional.empty();
        if (jpaDossier.isPresent()) {
            try {
                dossier = Optional.ofNullable(this.jpaDossierFactory.fromJpa(jpaDossier.get()));
            } catch (PatternSyntaxException | CommuneNotFoundException | PersonneParseException e) {
                log.error("{}", e);
            }
        }
        return dossier;
    }

    @Override
    public List<Dossier> findByDeposantId(String deposantId) {
        List<Dossier> dossiers = new ArrayList<Dossier>();
        List<JpaDossier> jpaDossiers = this.jpaSpringDossierRepository.findAllByDeposantIdWithStatuts(deposantId);
        jpaDossiers = this.jpaSpringDossierRepository.findAllByDeposantIdWithPiecesJointes(deposantId);
        jpaDossiers = this.jpaSpringDossierRepository.findAllByDeposantIdWithMessages(deposantId);
        jpaDossiers.forEach(jpaDossier -> {
            try {
                dossiers.add(this.jpaDossierFactory.fromJpa(jpaDossier));
            } catch (PatternSyntaxException | CommuneNotFoundException | PersonneParseException e) {
                log.error("{}", e);
            }
        });
        return dossiers;
    }

    @Override
    public Optional<Dossier> findByFichierId(String fichierId) {
        Optional<JpaDossier> jpaDossier = this.jpaSpringDossierRepository
                .findOneByPiecesJointesIdFichierIdWithPiecesJointes(fichierId);
        jpaDossier = this.jpaSpringDossierRepository.findOneByPiecesJointesIdFichierIdWithStatuts(fichierId);
        jpaDossier = this.jpaSpringDossierRepository.findOneByPiecesJointesIdFichierIdWithMessages(fichierId);
        Optional<Dossier> dossier = Optional.empty();
        if (jpaDossier.isPresent()) {
            log.debug("cerfa?={}", jpaDossier.get().cerfa().isPresent());
            log.debug("piecesJointes={}",
                    jpaDossier.get().getPiecesJointes().stream().map(pj -> pj.getId().getCode().getNumero()).toArray());
            try {
                dossier = Optional.ofNullable(this.jpaDossierFactory.fromJpa(jpaDossier.get()));
            } catch (PatternSyntaxException | CommuneNotFoundException | PersonneParseException e) {
                log.error("{}", e);
            }
        }
        return dossier;
    }

    @Override
    public List<Dossier> findByCommune(String commune) {
        List<Dossier> dossiers = new ArrayList<Dossier>();
        List<JpaDossier> jpaDossiers = this.jpaSpringDossierRepository
                .findAllByProjetAdresseCodePostalWithStatuts(commune);
        jpaDossiers = this.jpaSpringDossierRepository.findAllByProjetAdresseCodePostalWithPiecesJointes(commune);
        jpaDossiers = this.jpaSpringDossierRepository.findAllByProjetAdresseCodePostalWithMessages(commune);
        jpaDossiers.forEach(jpaDossier -> {
            try {
                dossiers.add(this.jpaDossierFactory.fromJpa(jpaDossier));
            } catch (PatternSyntaxException | CommuneNotFoundException | PersonneParseException e) {
                log.error("{}", e);
            }
        });
        return dossiers;
    }

    @Override
    public Dossier save(Dossier dossier) throws SaveDossierException {
        JpaDossier newJpaDossier = this.jpaDossierFactory.toJpa(dossier);
        Optional<JpaDossier> oldJpaDossier = this.jpaSpringDossierRepository
                .findOneByDossierIdWithPiecesJointes(dossier.identity().toString());
        oldJpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithStatuts(dossier.identity().toString());
        oldJpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithMessages(dossier.identity().toString());
        if (oldJpaDossier.isPresent()) {
            try {
                Dossier oldDossier = this.jpaDossierFactory.fromJpa(oldJpaDossier.get());
                for (JpaStatut newJpaStatut : newJpaDossier.getStatuts()) {
                    Statut newStatut = this.jpaStatutFactory.fromJpa(newJpaStatut);
                    if (!oldDossier.historiqueStatuts().contains(newStatut))
                        oldJpaDossier.get().addStatut(newJpaStatut);
                }
                for (JpaMessage newJpaMessage : newJpaDossier.getMessages()) {
                    Message newMessage = this.jpaMessageFactory.fromJpa(newJpaMessage);
                    if (!oldDossier.messages().contains(newMessage))
                        oldJpaDossier.get().addMessage(newJpaMessage);
                }
                for (JpaPieceJointe newJpaPieceJointe : newJpaDossier.getPiecesJointes()) {
                    PieceJointe newPieceJointe = this.jpaPieceJointeFactory.fromJpa(oldDossier, newJpaPieceJointe);
                    if (!newPieceJointe.isCerfa() && !newPieceJointe.isDecision() && !oldDossier.pieceJointes().contains(newPieceJointe))
                        oldJpaDossier.get().addPieceJointe(newJpaPieceJointe);
                }
            } catch (PatternSyntaxException | CommuneNotFoundException | PersonneParseException
                    | TypeStatutNotFoundException | UserParseException e) {
                throw new SaveDossierException(dossier, e);
            }
            this.jpaSpringDossierRepository.save(oldJpaDossier.get());
        } else {
            this.jpaSpringDossierRepository.save(newJpaDossier);
        }
        return dossier;
    }

    @Override
    public void delete(DossierId id) throws DossierNotFoundException {
        Optional<JpaDossier> jpaDossier = this.jpaSpringDossierRepository
                .findOneByDossierIdWithPiecesJointes(id.toString());
        jpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithMessages(id.toString());
        jpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithStatuts(id.toString());
        if (jpaDossier.isEmpty())
            throw new DossierNotFoundException(id);
        jpaDossier.get().removeAllChildren();
        this.jpaSpringDossierRepository.delete(jpaDossier.get());
    }

}