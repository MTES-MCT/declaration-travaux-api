package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;
import com.github.mtesmct.rieau.api.domain.repositories.SaveDossierException;
import com.github.mtesmct.rieau.api.infra.application.dossiers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(DossiersController.ROOT_URI)
@Slf4j
public class DossiersController {

	public static final String ROOT_URI = "/dossiers";
	public static final String PIECES_JOINTES_URI = "/piecesjointes";
	public static final String QUALIFIER_URI = "/qualifier";
	public static final String DECLARER_INCOMPLET_URI = "/declarer-incomplet";
	public static final String DECLARER_COMPLET_URI = "/declarer-complet";
	public static final String INSTRUIRE_URI = "/instruire";
	public static final String LANCER_CONSULTATIONS_URI = "/lancer-consultations";
	public static final String PRENDRE_DECISION_URI = "/prendre-decision";
	public static final String MESSAGES_URI = "/messages";

	@Autowired
	private TxImporterCerfaService importerCerfaService;
	@Autowired
	private TxAjouterPieceJointeService ajouterPieceJointeService;
	@Autowired
	private TxListerDossiersService listerDossiersService;
	@Autowired
	private TxConsulterDossierService consulterDossierService;
	@Autowired
	private TxQualifierDossierService qualifierDossierService;
	@Autowired
	private TxPrendreDecisionDossierService prendreDecisionDossierService;
	@Autowired
	private TxInstruireDossierService instruireDossierService;
	@Autowired
	private TxLancerConsultationsDossierService lancerConsultationsDossierService;
	@Autowired
	private TxDeclarerIncompletDossierService declarerIncompletDossierService;
	@Autowired
	private TxDeclarerCompletDossierService declarerCompletDossierService;
	@Autowired
	private TxAjouterMessageDossierService ajouterMessageDossierService;
	@Autowired
	private TxSupprimerDossierService supprimerDossierService;

	@Autowired
	private JsonDossierFactory jsonDossierFactory;

	@GetMapping("/{id}")
	public Optional<JsonDossier> consulter(@PathVariable String id)
			throws DeposantForbiddenException, AuthRequiredException, UserForbiddenException, UserInfoServiceException,
			MairieForbiddenException, InstructeurForbiddenException {
		Optional<Dossier> dossier = this.consulterDossierService.execute(id);
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@GetMapping
	List<JsonDossier> lister() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException {
		List<JsonDossier> dossiers = new ArrayList<JsonDossier>();
		this.listerDossiersService.execute().forEach(dossier -> this.addJsonDossier(dossiers, dossier));
		return dossiers;
	}

	private void addJsonDossier(List<JsonDossier> dossiers, Dossier dossier) {
		if (dossier != null) {
			JsonDossier jsonDossier = this.jsonDossierFactory.toJson(dossier);
			dossiers.add(jsonDossier);
		}
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void ajouterCerfa(@RequestParam("file") MultipartFile file) throws IOException, DossierImportException,
			AuthRequiredException, UserForbiddenException, UserInfoServiceException, StatutForbiddenException,
			TypeStatutNotFoundException, PieceNonAJoindreException, TypeDossierNotFoundException, SaveDossierException {
		this.importerCerfaService.execute(file.getInputStream(), file.getOriginalFilename(), file.getContentType(),
				file.getSize());
	}

	@PostMapping(path = "/{id}" + PIECES_JOINTES_URI + "/{numero}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void ajouterPieceJointe(@PathVariable String id, @PathVariable String numero,
			@RequestParam("file") MultipartFile file) throws IOException, DeposantForbiddenException,
			AuthRequiredException, UserForbiddenException, UserInfoServiceException, AjouterPieceJointeException,
			SaveDossierException {
		this.ajouterPieceJointeService.execute(new DossierId(id), numero, file.getInputStream(),
				file.getOriginalFilename(), file.getContentType(), file.getSize());
	}

	@PostMapping("/{id}" + QUALIFIER_URI)
	public Optional<JsonDossier> qualifier(@PathVariable String id)
			throws AuthRequiredException, UserForbiddenException, UserInfoServiceException, MairieForbiddenException,
			DossierNotFoundException, TypeStatutNotFoundException, StatutForbiddenException, SaveDossierException {
		Optional<Dossier> dossier = this.qualifierDossierService.execute(new DossierId(id));
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@PostMapping("/{id}" + INSTRUIRE_URI)
	public Optional<JsonDossier> instruire(@PathVariable String id)
			throws AuthRequiredException, UserForbiddenException, UserInfoServiceException, DossierNotFoundException,
			TypeStatutNotFoundException, StatutForbiddenException, InstructeurForbiddenException, SaveDossierException {
		Optional<Dossier> dossier = this.instruireDossierService.execute(new DossierId(id));
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@PostMapping("/{id}" + DECLARER_INCOMPLET_URI)
	public Optional<JsonDossier> declarerIncomplet(@PathVariable String id, @RequestBody String message)
			throws AuthRequiredException, UserForbiddenException, UserInfoServiceException,
			InstructeurForbiddenException, DossierNotFoundException, TypeStatutNotFoundException,
			StatutForbiddenException, SaveDossierException {
		Optional<Dossier> dossier = this.declarerIncompletDossierService.execute(new DossierId(id), message);
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@PostMapping("/{id}" + DECLARER_COMPLET_URI)
	public Optional<JsonDossier> declarerComplet(@PathVariable String id) throws AuthRequiredException,
			UserForbiddenException, UserInfoServiceException, InstructeurForbiddenException, DossierNotFoundException,
			TypeStatutNotFoundException, StatutForbiddenException, SaveDossierException {
		Optional<Dossier> dossier = this.declarerCompletDossierService.execute(new DossierId(id));
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@PostMapping("/{id}" + LANCER_CONSULTATIONS_URI)
	public Optional<JsonDossier> lancerSupprimerations(@PathVariable String id)
			throws AuthRequiredException, UserForbiddenException, UserInfoServiceException, DossierNotFoundException,
			TypeStatutNotFoundException, StatutForbiddenException, InstructeurForbiddenException, SaveDossierException {
		Optional<Dossier> dossier = this.lancerConsultationsDossierService.execute(new DossierId(id));
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@PostMapping(path = "/{id}" + PRENDRE_DECISION_URI, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Optional<JsonDossier> prendreDecision(@PathVariable String id, @RequestParam("file") MultipartFile file)
			throws IOException, AuthRequiredException, UserForbiddenException, UserInfoServiceException,
			AjouterPieceJointeException, DossierNotFoundException, MairieForbiddenException,
			TypeStatutNotFoundException, StatutForbiddenException, SaveDossierException {
		Optional<JsonDossier> jsonDossier = Optional.empty();
		Optional<Dossier> dossier = this.prendreDecisionDossierService.execute(new DossierId(id), file.getInputStream(),
				file.getOriginalFilename(), file.getContentType(), file.getSize());
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@PostMapping("/{id}" + MESSAGES_URI)
	public Optional<JsonDossier> ajouterMessage(@PathVariable String id, @RequestBody String message)
			throws AuthRequiredException, UserForbiddenException, UserInfoServiceException,
			InstructeurForbiddenException, DossierNotFoundException, TypeStatutNotFoundException,
			StatutForbiddenException, DeposantForbiddenException, SaveDossierException {
		Optional<Dossier> dossier = this.ajouterMessageDossierService.execute(new DossierId(id), message);
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent()) {
			for (Message msg : dossier.get().messages()) {
				log.debug("dossier message={}", msg);
			}
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		}
		return jsonDossier;
	}

	@DeleteMapping("/{id}")
	public void supprimer(@PathVariable String id)
			throws DeposantForbiddenException, AuthRequiredException, UserForbiddenException, UserInfoServiceException,
			MairieForbiddenException, InstructeurForbiddenException, DossierNotFoundException {
		this.supprimerDossierService.execute(id);
	}
}