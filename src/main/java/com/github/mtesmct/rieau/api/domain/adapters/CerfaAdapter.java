package com.github.mtesmct.rieau.api.domain.adapters;

import com.github.mtesmct.rieau.api.domain.entities.Cerfa;
import com.github.mtesmct.rieau.api.domain.entities.Depot;

public interface CerfaAdapter {
    public Depot fromCerfa(String depositaire, Cerfa cerfa) throws CerfaAdapterException;
}