package com.github.mtesmct.rieau.api.depositaire.domain.services;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Cerfa;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.CerfaAdapterException;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DateRepository;

public class CerfaAdapter {

    private DateRepository dateRepository;
    private NoNationalService noNationalService;

    public Depot fromCerfa(Cerfa cerfa) throws CerfaAdapterException {
		Map<String, Type> codesTypes = new HashMap<String, Type>();
		codesTypes.put("N° 13703*06", Type.dp);
		codesTypes.put("N° 13406*06", Type.pcmi);
        Type type = codesTypes.get(cerfa.getCode());
		String acceptedCodes = codesTypes.keySet().stream().map(k -> k.toString())
				.collect(Collectors.joining(", ", "[", "]"));
		if (type == null)
			throw new CerfaAdapterException(
					"Le code CERFA est inconnu: " + cerfa.getCode() + ". Les seuls codes reconnus sont: " + acceptedCodes);
        Depot depot = new Depot(this.noNationalService.getNew(), type, this.dateRepository.now());
        return depot;
    }

    public CerfaAdapter(DateRepository dateRepository, NoNationalService noNationalService) {
        this.dateRepository = dateRepository;
        this.noNationalService = noNationalService;
    }
}