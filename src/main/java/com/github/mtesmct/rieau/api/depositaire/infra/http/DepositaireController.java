package com.github.mtesmct.rieau.api.depositaire.infra.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.infra.adau.ADAUFileDossierService;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.depositaire.infra.file.upload.FileUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(DepositaireController.ROOT_URL)
public class DepositaireController {

	public static final String ROOT_URL = "/depots";

	@Autowired
	private Depositaire depositaire;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;

	@Autowired
	private DepotWebAdapter adapter;
	
	@Autowired
	private ADAUFileDossierService dossierService;
	
	@Autowired
	private FileUploadService fileUploadService;

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

	@PostMapping
	public void ajouteDepot(@RequestParam("file") MultipartFile file) throws IOException {
		File uploadedFile = this.fileUploadService.store(file.getOriginalFilename(), file.getInputStream());
		this.dossierService.importerDepot(uploadedFile);
	}

}