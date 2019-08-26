package com.github.mtesmct.rieau.api.depositaire.domain.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdDepotRepository;

public class Depositaire implements Serializable {
    private static final long serialVersionUID = 1L;
    private DepotRepository repository;
    private DemandeRepository demandeRepository;
    private DateRepository dateRepository;
    private IdDepotRepository idDepotRepository;

    public Depositaire(DepotRepository repository, DateRepository dateRepository, IdDepotRepository idDepotRepository) {
        this.repository = repository;
        this.dateRepository = dateRepository;
        this.idDepotRepository = idDepotRepository;
    }

    public Optional<Depot> ajouterDepot() {
        Depot depot = this.demandeRepository.lireDemande();
        return Optional.ofNullable(this.repository.save(depot));
    }

    public List<Depot> listeMesDepots() {
        return this.repository.findAll();
    }

    public Optional<Depot> trouveMonDepot(String id) {
        return this.repository.findById(id);
    }

}