package com.github.mtesmct.rieau.rieauapi.infra.http;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.rieauapi.domain.entities.Demande;
import com.github.mtesmct.rieau.rieauapi.domain.entities.Depositaire;

import org.springframework.beans.factory.annotation.Autowired;
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

	@GetMapping("/{id}")
	public Optional<Demande> trouveMaDemande(@PathVariable String id) {
		return this.depositaire.trouveMaDemande(id);
	}

	@GetMapping
	List<Demande> listeMesDemandes() {
		return this.depositaire.listeSesDemandes();
	}

}