package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaDossierFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDossierRepository implements DossierRepository {

    @Autowired
    private JpaSpringDossierRepository jpaSpringDossierRepository;
    @Autowired
    private JpaDossierFactory jpaDossierFactory;

    @Override
    public Optional<Dossier> findById(String id) {
        Optional<JpaDossier> jpaEntity = this.jpaSpringDossierRepository.findBySimpleNaturalId(id);
        Optional<Dossier> dossier = Optional.empty();
        if (jpaEntity.isPresent()) {
            dossier = Optional.ofNullable(this.jpaDossierFactory.fromJpa(jpaEntity.get()));
        }
        return dossier;
    }

    @Override
    public List<Dossier> findByDeposantId(String deposantId) {
        List<Dossier> dossiers = new ArrayList<Dossier>();
        this.jpaSpringDossierRepository.findAllByDeposantId(deposantId)
                .forEach(jpaEntity -> dossiers.add(this.jpaDossierFactory.fromJpa(jpaEntity)));
        return dossiers;
    }

    @Override
    public Dossier save(Dossier dossier) {
        JpaDossier jpaDossierAfter = this.jpaDossierFactory.toJpa(dossier);
        Optional<JpaDossier> jpaDossierBefore = this.jpaSpringDossierRepository
                .findBySimpleNaturalId(dossier.identity().toString());
        if (jpaDossierBefore.isPresent()) {
            jpaDossierAfter.setId(jpaDossierBefore.get().getId());
            if (!dossier.pieceJointes().isEmpty())
                dossier.pieceJointes().forEach(pieceJointe -> savePieceJointe(jpaDossierBefore.get(), pieceJointe));
        }
        jpaDossierAfter = this.jpaSpringDossierRepository.save(jpaDossierAfter);
        return this.jpaDossierFactory.fromJpa(jpaDossierAfter);
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