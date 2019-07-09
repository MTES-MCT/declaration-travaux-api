package com.github.mtesmct.rieau.rieauapi.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.rieauapi.domain.entities.Demande;

public interface DepositaireRepository {
    public List<Demande> findAll();
    public Optional<Demande> findById(String id);
    public Demande save(Demande demande);
}