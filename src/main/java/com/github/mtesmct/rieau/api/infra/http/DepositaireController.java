package com.github.mtesmct.rieau.api.infra.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Demande;
import com.github.mtesmct.rieau.api.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demandes")
@Secured("ROLE_DEPOSITAIRE")
public class DepositaireController {

	@Autowired
	private Depositaire depositaire;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;

	@Autowired
	private DemandeAdapter adapter;

	@GetMapping("/{id}")
	public Optional<JsonDemande> trouveMaDemande(@PathVariable String id) {
		Optional<Demande> demande = this.depositaire.trouveMaDemande(id);
		return Optional.ofNullable(this.adapter.toJson(demande.get()));
	}

	@GetMapping
	List<JsonDemande> listeMesDemandes() {
		List<JsonDemande> demandes = new ArrayList<JsonDemande>();
		this.depositaire.listeMesDemandes().forEach(demande -> demandes.add(this.adapter.toJson(demande)));
		return demandes;
	}

}