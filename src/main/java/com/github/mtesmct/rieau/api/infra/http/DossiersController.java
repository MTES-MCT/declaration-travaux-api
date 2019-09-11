package com.github.mtesmct.rieau.api.infra.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.ConsulterMonDossierService;
import com.github.mtesmct.rieau.api.application.dossiers.ImporterCerfaService;
import com.github.mtesmct.rieau.api.application.dossiers.ListerMesDossiersService;
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
@RequestMapping(DossiersController.ROOT_URL)
public class DossiersController {

	public static final String ROOT_URL = "/dossiers";

	@Autowired
	private ImporterCerfaService importerCerfaService;
	@Autowired
	private ListerMesDossiersService listerMesDossiersService;
	@Autowired
	private ConsulterMonDossierService consulterMonDossierService;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;

	@Autowired
	private JsonDossierFactory adapter;
	
	@Autowired
	private FileUploadService fileUploadService;

	@GetMapping("/{id}")
	public Optional<JsonDossier> donne(@PathVariable String id) {
        return this.adapter.toJson(this.consulterMonDossierService.execute(id));
	}

	@GetMapping
	List<JsonDossier> liste() {
		List<JsonDossier> dossiers = new ArrayList<JsonDossier>();
		this.listerMesDossiersService.execute().forEach(dossier -> dossiers.add(this.adapter.toJson(Optional.ofNullable(dossier)).get()));
		return dossiers;
	}

	@PostMapping
	public void ajoute(@RequestParam("file") MultipartFile file) throws IOException {
		File uploadedFile = this.fileUploadService.store(file.getOriginalFilename(), file.getInputStream());
		this.importerCerfaService.execute(uploadedFile);
	}

}