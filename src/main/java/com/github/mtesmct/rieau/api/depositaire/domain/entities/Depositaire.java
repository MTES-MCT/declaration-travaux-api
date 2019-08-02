package com.github.mtesmct.rieau.api.depositaire.domain.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;

public class Depositaire implements Serializable {
    private static final long serialVersionUID = 1L;
    private DepotRepository repository;
    private DateRepository dateRepository;

    public Depositaire(DepotRepository repository, DateRepository dateRepository) {
        this.repository = repository;
        this.dateRepository = dateRepository;
    }

    public void depose(Depot depot) {
        depot.setDate(this.dateRepository.now());
        this.repository.save(depot);
    }

    public List<Depot> listeMesDepots() {
        return this.repository.findAll();
    }

    public Optional<Depot> trouveMonDepot(String id) {
        return this.repository.findById(id);
    }

}