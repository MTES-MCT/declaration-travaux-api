package com.github.mtesmct.rieau.api.infra.http.dossiers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.InstructeurForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxAjouterMessageDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxAjouterPieceJointeService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxConsulterDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxDeclarerCompletDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxDeclarerIncompletDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxImporterCerfaService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxInstruireDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxLancerConsultationsDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxListerDossiersService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxPrendreDecisionDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxQualifierDossierService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxSupprimerDossierService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(DossiersController.ROOT_URI)
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
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;

	@Autowired
	private JsonDossierFactory jsonDossierFactory;

	@GetMapping("/{id}")
	public Optional<JsonDossier> consulter(@PathVariable String id) throws DeposantForbiddenException,
			AuthRequiredException, UserForbiddenException, UserInfoServiceException, MairieForbiddenException,
			InstructeurForbiddenException {
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
			TypeStatutNotFoundException, PieceNonAJoindreException, TypeDossierNotFoundException {
		this.importerCerfaService.execute(file.getInputStream(), file.getOriginalFilename(), file.getContentType(),
				file.getSize());
	}

	@PostMapping(path = "/{id}" + PIECES_JOINTES_URI + "/{numero}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void ajouterPieceJointe(@PathVariable String id, @PathVariable String numero,
			@RequestParam("file") MultipartFile file) throws IOException, DeposantForbiddenException,
			AuthRequiredException, UserForbiddenException, UserInfoServiceException, AjouterPieceJointeException {
		this.ajouterPieceJointeService.execute(new DossierId(id), numero, file.getInputStream(),
				file.getOriginalFilename(), file.getContentType(), file.getSize());
	}

	@PostMapping("/{id}" + QUALIFIER_URI)
	public Optional<JsonDossier> qualifier(@PathVariable String id)
			throws AuthRequiredException, UserForbiddenException, UserInfoServiceException, MairieForbiddenException,
			DossierNotFoundException, TypeStatutNotFoundException, StatutForbiddenException {
		Optional<Dossier> dossier = this.qualifierDossierService.execute(new DossierId(id));
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@PostMapping("/{id}" + INSTRUIRE_URI)
	public Optional<JsonDossier> instruire(@PathVariable String id)
			throws AuthRequiredException, UserForbiddenException, UserInfoServiceException, DossierNotFoundException,
			TypeStatutNotFoundException, StatutForbiddenException, InstructeurForbiddenException {
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
			StatutForbiddenException {
		Optional<Dossier> dossier = this.declarerIncompletDossierService.execute(new DossierId(id), message);
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@PostMapping("/{id}" + DECLARER_COMPLET_URI)
	public Optional<JsonDossier> declarerComplet(@PathVariable String id) throws AuthRequiredException,
			UserForbiddenException, UserInfoServiceException, InstructeurForbiddenException, DossierNotFoundException,
			TypeStatutNotFoundException, StatutForbiddenException {
		Optional<Dossier> dossier = this.declarerCompletDossierService.execute(new DossierId(id));
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}

	@PostMapping("/{id}" + LANCER_CONSULTATIONS_URI)
	public Optional<JsonDossier> lancerSupprimerations(@PathVariable String id)
			throws AuthRequiredException, UserForbiddenException, UserInfoServiceException, DossierNotFoundException,
			TypeStatutNotFoundException, StatutForbiddenException, InstructeurForbiddenException {
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
			TypeStatutNotFoundException, StatutForbiddenException {
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
			StatutForbiddenException, DeposantForbiddenException {
		Optional<Dossier> dossier = this.ajouterMessageDossierService.execute(new DossierId(id), message);
		Optional<JsonDossier> jsonDossier = Optional.empty();
		if (dossier.isPresent())
			jsonDossier = Optional.ofNullable(this.jsonDossierFactory.toJson(dossier.get()));
		return jsonDossier;
	}


	@DeleteMapping("/{id}")
	public void supprimer(@PathVariable String id) throws DeposantForbiddenException,
			AuthRequiredException, UserForbiddenException, UserInfoServiceException, MairieForbiddenException,
			InstructeurForbiddenException, DossierNotFoundException {
		this.supprimerDossierService.execute(id);
	}
}