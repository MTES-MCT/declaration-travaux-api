package com.github.mtesmct.rieau.api.infra.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantNonAutoriseException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxAjouterPieceJointeService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxConsulterMonDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxImporterCerfaService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxListerMesDossiersService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

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
	private TxImporterCerfaService importerCerfaService;
	@Autowired
	private TxAjouterPieceJointeService ajouterPieceJointeService;
	@Autowired
	private TxListerMesDossiersService listerMesDossiersService;
	@Autowired
	private TxConsulterMonDossierService consulterMonDossierService;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;

	@Autowired
	private JsonDossierFactory jsonDossierFactory;

	@GetMapping("/{id}")
	public Optional<JsonDossier> consulter(@PathVariable String id)
			throws DeposantNonAutoriseException, AuthRequiredException, UserForbiddenException, UserServiceException {
		return this.jsonDossierFactory.toJson(this.consulterMonDossierService.execute(id));
	}

	@GetMapping
	List<JsonDossier> lister() throws AuthRequiredException, UserForbiddenException, UserServiceException {
		List<JsonDossier> dossiers = new ArrayList<JsonDossier>();
		this.listerMesDossiersService.execute().forEach(dossier -> this.addJsonDossier(dossiers, dossier));
		return dossiers;
	}

	private void addJsonDossier(List<JsonDossier> dossiers, Dossier dossier) {
		Optional<JsonDossier> jsonDossier = this.jsonDossierFactory.toJson(Optional.ofNullable(dossier));
		if (jsonDossier.isPresent())
			dossiers.add(jsonDossier.get());
	}

	@PostMapping
	public void ajouterCerfa(@RequestParam("file") MultipartFile file) throws IOException, DossierImportException,
			AuthRequiredException, UserForbiddenException, UserServiceException {
		Fichier fichier = new Fichier(file.getOriginalFilename(), file.getContentType(), file.getInputStream(), file.getSize());
		this.importerCerfaService.execute(fichier);
	}

	@PostMapping("/{id}/piecesjointes/{numero}")
	public void ajouterPieceJointe(@PathVariable String id, @PathVariable String numero, @RequestParam("file") MultipartFile file) throws IOException, DeposantNonAutoriseException,
			AuthRequiredException, UserForbiddenException, UserServiceException {
		Fichier fichier = new Fichier(file.getOriginalFilename(), file.getContentType(), file.getInputStream(), file.getSize());
		Optional<Dossier> dossier = this.consulterMonDossierService.execute(id);
		this.ajouterPieceJointeService.execute(dossier.get(), numero, fichier);
	}

}