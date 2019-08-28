package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Depot;
import com.github.mtesmct.rieau.api.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.adapters.JpaDepotAdapter;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDepot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaDepotRepository implements DepotRepository {

    @Autowired
    private JpaSpringDepotRepository jpaSpringRepository;
    @Autowired
    private JpaDepotAdapter adapter;

    @Override
    public Optional<Depot> findByDepositaireAndId(String depositaire, String id) {
        Optional<JpaDepot> jpaEntity = this.jpaSpringRepository.findOneByDepositaireAndNoNational(depositaire, id);
        Optional<Depot> depot = Optional.empty();
        if (jpaEntity.isPresent()) {
            depot = Optional.ofNullable(this.adapter.fromJpa(jpaEntity.get()));
        }
        return depot;
    }

    @Override
    public List<Depot> findByDepositaire(String depositaire) {
        List<Depot> depots = new ArrayList<Depot>();
        this.jpaSpringRepository.findByDepositaire(depositaire).forEach(jpaEntity -> depots.add(this.adapter.fromJpa(jpaEntity)));
        return depots;
    }

    @Override
    @Transactional
    public Depot save(Depot depot) {
        JpaDepot jpaDepot = this.adapter.toJpa(depot);
        this.jpaSpringRepository.save(jpaDepot);
        return depot;
    }
    
}