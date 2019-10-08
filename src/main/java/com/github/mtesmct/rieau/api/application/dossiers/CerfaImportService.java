package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Map;
import java.util.Set;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;

public interface CerfaImportService {
    public Map<String,String> lire(Fichier fichier) throws CerfaImportException;
    public Set<String> keys(TypesDossier type);
}