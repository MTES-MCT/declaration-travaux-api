package com.github.mtesmct.rieau.api.application;

import java.io.File;
import java.io.Serializable;
import java.security.Principal;
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

    public List<Depot> liste(Principal principal) {
        return this.repository.findAll();
    }

    public Optional<Depot> donne(Principal principal, String id) {
        return this.repository.findById(id);
    }

    public Optional<Depot> importe(Principal principal, File file) throws DepotImportException {
        Cerfa cerfa  = this.cerfaService.lire(file);
        Depot depot = this.cerfaAdapter.fromCerfa(cerfa);
        return Optional.ofNullable(this.repository.save(depot));
    }

}