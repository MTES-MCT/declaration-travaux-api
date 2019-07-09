package com.github.mtesmct.rieau.rieauapi.domain.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.rieauapi.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.rieauapi.domain.repositories.DepositaireRepository;

public class Depositaire implements Serializable {
    private static final long serialVersionUID = 1L;
    private DepositaireRepository repository;
    private DateRepository dateRepository;

    public Depositaire(DepositaireRepository repository, DateRepository dateRepository) {
        this.repository = repository;
        this.dateRepository = dateRepository;
    }

    public void depose(Demande demande) {
        demande.setDate(this.dateRepository.now());
        this.repository.save(demande);
    }

    public List<Demande> listeSesDemandes() {
        return this.repository.findAll();
    }

    public Optional<Demande> trouveMaDemande(String id) {
        return this.repository.findById(id);
    }

}