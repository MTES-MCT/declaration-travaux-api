package com.github.mtesmct.rieau.api.infra.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.infra.application.dossiers.SpringConsulterMonDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.SpringImporterCerfaService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.SpringListerMesDossiersService;
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
	private SpringImporterCerfaService importerCerfaService;
	@Autowired
	private SpringListerMesDossiersService listerMesDossiersService;
	@Autowired
	private SpringConsulterMonDossierService consulterMonDossierService;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;

	@Autowired
	private JsonDossierFactory jsonDossierFactory;
	
	@Autowired
	private FileUploadService fileUploadService;

	@GetMapping("/{id}")
	public Optional<JsonDossier> consulter(@PathVariable String id) {
        return this.jsonDossierFactory.toJson(this.consulterMonDossierService.execute(id));
	}

	@GetMapping
	List<JsonDossier> lister() {
		List<JsonDossier> dossiers = new ArrayList<JsonDossier>();
		this.listerMesDossiersService.execute().forEach(dossier -> dossiers.add(this.jsonDossierFactory.toJson(Optional.ofNullable(dossier)).get()));
		return dossiers;
	}

	@PostMapping
	public void ajouter(@RequestParam("file") MultipartFile file) throws IOException {
		File uploadedFile = this.fileUploadService.store(file.getOriginalFilename(), file.getInputStream());
		this.importerCerfaService.execute(uploadedFile);
	}

}