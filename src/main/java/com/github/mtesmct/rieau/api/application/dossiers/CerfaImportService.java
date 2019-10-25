package com.github.mtesmct.rieau.api.application.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;

import java.util.Map;
import java.util.Set;

public interface CerfaImportService {
    public Map<String,String> lire(Fichier fichier) throws CerfaImportException;
    public Set<String> keys(EnumTypes type);
}