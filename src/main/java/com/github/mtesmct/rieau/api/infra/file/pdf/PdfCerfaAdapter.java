package com.github.mtesmct.rieau.api.infra.file.pdf;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.mtesmct.rieau.api.domain.adapters.CerfaAdapter;
import com.github.mtesmct.rieau.api.domain.adapters.CerfaAdapterException;
import com.github.mtesmct.rieau.api.domain.entities.Cerfa;
import com.github.mtesmct.rieau.api.domain.entities.Depot;
import com.github.mtesmct.rieau.api.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.application.NoNationalService;

public class PdfCerfaAdapter implements CerfaAdapter {

    private DateRepository dateRepository;
    private NoNationalService noNationalService;
    private Principal principal;

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
        Depot depot = new Depot(this.noNationalService.getNew(), type, this.dateRepository.now(), principal.getName());
        return depot;
    }

    public PdfCerfaAdapter(DateRepository dateRepository, NoNationalService noNationalService) {
        this.dateRepository = dateRepository;
        this.noNationalService = noNationalService;
    }
}