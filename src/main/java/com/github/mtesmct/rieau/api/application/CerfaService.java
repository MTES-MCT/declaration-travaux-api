package com.github.mtesmct.rieau.api.application;

import java.io.File;

import com.github.mtesmct.rieau.api.domain.entities.Cerfa;

public interface CerfaService {
    public Cerfa lire(File file) throws DepotImportException;
}