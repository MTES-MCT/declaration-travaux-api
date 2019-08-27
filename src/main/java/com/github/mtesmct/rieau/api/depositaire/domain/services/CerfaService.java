package com.github.mtesmct.rieau.api.depositaire.domain.services;

import java.io.File;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Cerfa;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.DepotImportException;

public interface CerfaService {
    public Cerfa lireCerfa(File file) throws DepotImportException;
}