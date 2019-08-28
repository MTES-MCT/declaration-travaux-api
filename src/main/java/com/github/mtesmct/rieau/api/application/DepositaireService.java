package com.github.mtesmct.rieau.api.application;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.adapters.CerfaAdapter;
import com.github.mtesmct.rieau.api.domain.entities.Cerfa;
import com.github.mtesmct.rieau.api.domain.entities.Depot;
import com.github.mtesmct.rieau.api.domain.repositories.DepotRepository;

public class DepositaireService implements Serializable {
    private static final long serialVersionUID = 1L;
    private DepotRepository repository;
    private CerfaService cerfaService;
    private CerfaAdapter cerfaAdapter;

    public DepositaireService(DepotRepository repository, CerfaService cerfaService, CerfaAdapter cerfaAdapter) {
        this.repository = repository;
        this.cerfaService = cerfaService;
        this.cerfaAdapter = cerfaAdapter;
    }

    public List<Depot> liste(String depositaire) {
        return this.repository.findByDepositaire(depositaire);
    }

    public Optional<Depot> donne(String depositaire, String id) {
        return this.repository.findByDepositaireAndId(depositaire,id);
    }

    public Optional<Depot> importe(String depositaire, File file) throws DepotImportException {
        Cerfa cerfa  = this.cerfaService.lire(file);
        Depot depot = this.cerfaAdapter.fromCerfa(depositaire, cerfa);
        return Optional.ofNullable(this.repository.save(depot));
    }

}