package com.github.mtesmct.rieau.api.depositaire.domain.entities;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.services.CerfaAdapter;
import com.github.mtesmct.rieau.api.depositaire.domain.services.CerfaService;

public class Depositaire implements Serializable {
    private static final long serialVersionUID = 1L;
    private DepotRepository repository;
    private CerfaService cerfaService;
    private CerfaAdapter cerfaAdapter;

    public Depositaire(DepotRepository repository, CerfaService cerfaService, CerfaAdapter cerfaAdapter) {
        this.repository = repository;
        this.cerfaService = cerfaService;
        this.cerfaAdapter = cerfaAdapter;
    }

    public List<Depot> listeMesDepots() {
        return this.repository.findAll();
    }

    public Optional<Depot> trouveMonDepot(String id) {
        return this.repository.findById(id);
    }

    public Optional<Depot> importerDepot(File file) throws DepotImportException {
        Cerfa cerfa  = this.cerfaService.lireCerfa(file);
        Depot depot = this.cerfaAdapter.fromCerfa(cerfa);
        return Optional.ofNullable(this.repository.save(depot));
    }

}