package com.github.mtesmct.rieau.api.depositaire.domain.services;

import java.io.File;

public interface DossierService {
    public void importerDepot(File file) throws DepotImportException;
}