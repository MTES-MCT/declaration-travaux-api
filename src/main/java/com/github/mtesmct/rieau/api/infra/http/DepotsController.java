package com.github.mtesmct.rieau.api.infra.http;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.DepositaireService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.file.upload.FileUploadService;

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
@RequestMapping(DepotsController.ROOT_URL)
public class DepotsController {

	public static final String ROOT_URL = "/depots";

	@Autowired
	private DepositaireService depositaireService;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;

	@Autowired
	private JsonDepotAdapter adapter;
	
	@Autowired
	private FileUploadService fileUploadService;

	@GetMapping("/{id}")
	public Optional<JsonDepot> donne(Principal principal, @PathVariable String id) {
        return this.adapter.toJson(this.depositaireService.donne(principal.getName(), id));
	}

	@GetMapping
	List<JsonDepot> liste(Principal principal) {
		List<JsonDepot> depots = new ArrayList<JsonDepot>();
		this.depositaireService.liste(principal.getName()).forEach(depot -> depots.add(this.adapter.toJson(Optional.ofNullable(depot)).get()));
		return depots;
	}

	@PostMapping
	public void ajoute(Principal principal, @RequestParam("file") MultipartFile file) throws IOException {
		File uploadedFile = this.fileUploadService.store(file.getOriginalFilename(), file.getInputStream());
		this.depositaireService.importe(principal.getName(), uploadedFile);
	}

}