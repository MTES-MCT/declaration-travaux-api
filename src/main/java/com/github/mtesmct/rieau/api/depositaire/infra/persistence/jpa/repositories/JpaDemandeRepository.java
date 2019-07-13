package com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Demande;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DemandeRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.adapters.DemandePersistenceAdapter;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaDemande;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaDemandeRepository implements DemandeRepository {

    @Autowired
    private JpaSpringDemandeRepository jpaSpringRepository;
    @Autowired
    private DemandePersistenceAdapter adapter;

    @Override
    public Optional<Demande> findById(String id) {
        Optional<JpaDemande> jpaEntity = this.jpaSpringRepository.findById(id);
        Optional<Demande> demande = Optional.empty();
        if (jpaEntity.isPresent()) {
            demande = Optional.ofNullable(this.adapter.fromJpa(jpaEntity.get()));
        }
        return demande;
    }

    @Override
    public List<Demande> findAll() {
        List<Demande> demandes = new ArrayList<Demande>();
        this.jpaSpringRepository.findAll().forEach(jpaEntity -> demandes.add(this.adapter.fromJpa(jpaEntity)));
        return demandes;
    }

    @Override
    @Transactional
    public Demande save(Demande demande) {
        JpaDemande jpaDemande = this.adapter.toJpa(demande);
        this.jpaSpringRepository.save(jpaDemande);
        demande.setId(jpaDemande.getId());
        return demande;
    }
    
}