package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.File;
import java.util.Optional;

public interface CerfaImportService {
    public Optional<String> lireCode(File file) throws DossierImportException;
}