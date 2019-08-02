package com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.adapters.DepotPersistenceAdapter;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaDepot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaDepotRepository implements DepotRepository {

    @Autowired
    private JpaSpringDepotRepository jpaSpringRepository;
    @Autowired
    private DepotPersistenceAdapter adapter;

    @Override
    public Optional<Depot> findById(String id) {
        Optional<JpaDepot> jpaEntity = this.jpaSpringRepository.findById(id);
        Optional<Depot> depot = Optional.empty();
        if (jpaEntity.isPresent()) {
            depot = Optional.ofNullable(this.adapter.fromJpa(jpaEntity.get()));
        }
        return depot;
    }

    @Override
    public List<Depot> findAll() {
        List<Depot> depots = new ArrayList<Depot>();
        this.jpaSpringRepository.findAll().forEach(jpaEntity -> depots.add(this.adapter.fromJpa(jpaEntity)));
        return depots;
    }

    @Override
    @Transactional
    public Depot save(Depot depot) {
        JpaDepot jpaDepot = this.adapter.toJpa(depot);
        this.jpaSpringRepository.save(jpaDepot);
        depot.setId(jpaDepot.getId());
        return depot;
    }
    
}