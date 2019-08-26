package com.github.mtesmct.rieau.api.depositaire.domain.repositories;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;

public interface DemandeRepository {
    public Depot lireDemande();
}