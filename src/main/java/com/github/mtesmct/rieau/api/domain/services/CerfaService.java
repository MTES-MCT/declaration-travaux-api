package com.github.mtesmct.rieau.api.domain.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;

@DomainService
public class CerfaService {
    private Map<String, TypeDossier> codes;

    public CerfaService() {
        this.codes = new HashMap<String, TypeDossier>();
        this.codes.put("13406", TypeDossier.PCMI);
        this.codes.put("13703", TypeDossier.DP);
    }

    public Optional<TypeDossier> fromCodeCerfa(String code) throws CerfaServiceException {
        Optional<TypeDossier> type = Optional.empty();
        try{
            type = Optional.ofNullable(codes.get(code));
        } catch (ClassCastException | NullPointerException e) {
            throw new CerfaServiceException("Type de dossier pour le code CERFA" + code + " impossible à identifier", e);
        }
        return type;
    }
}