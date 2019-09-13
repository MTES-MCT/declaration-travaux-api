package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaDossierFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaDossierRepository implements DossierRepository {

    @Autowired
    private JpaSpringDossierRepository jpaSpringRepository;
    @Autowired
    private JpaDossierFactory jpaDossierFactory;

    @Override
    public Optional<Dossier> findByDeposantIdAndId(String deposantId, String id) {
        Optional<JpaDossier> jpaEntity = this.jpaSpringRepository.findOptionalByDeposantIdAndDossierId(deposantId, id);
        Optional<Dossier> dossier = Optional.empty();
        if (jpaEntity.isPresent()) {
            dossier = Optional.ofNullable(this.jpaDossierFactory.fromJpa(jpaEntity.get()));
        }
        return dossier;
    }

    @Override
    public List<Dossier> findByDeposantId(String deposantId) {
        List<Dossier> dossiers = new ArrayList<Dossier>();
        this.jpaSpringRepository.findAllByDeposantId(deposantId).forEach(jpaEntity -> dossiers.add(this.jpaDossierFactory.fromJpa(jpaEntity)));
        return dossiers;
    }

    @Override
    @Transactional
    public Dossier save(Dossier dossier) {
        JpaDossier jpaDossier = this.jpaDossierFactory.toJpa(dossier);
        this.jpaSpringRepository.save(jpaDossier);
        return dossier;
    }
    
}