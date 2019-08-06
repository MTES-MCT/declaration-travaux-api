package com.github.mtesmct.rieau.api.depositaire.infra.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/depots")
public class DepositaireController {

	@Autowired
	private Depositaire depositaire;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;

	@Autowired
	private DepotWebAdapter adapter;

	@GetMapping("/{id}")
	public Optional<JsonDepot> trouveMonDepot(@PathVariable String id) {
		Optional<Depot> depot = this.depositaire.trouveMonDepot(id);
		Optional<JsonDepot> jsonDepot = Optional.empty();
        if (depot.isPresent()) {
            jsonDepot = Optional.ofNullable(this.adapter.toJson(depot.get()));
        }
        return jsonDepot;
	}

	@GetMapping
	List<JsonDepot> listeMesDepots() {
		List<JsonDepot> depots = new ArrayList<JsonDepot>();
		this.depositaire.listeMesDepots().forEach(depot -> depots.add(this.adapter.toJson(depot)));
		return depots;
	}

}