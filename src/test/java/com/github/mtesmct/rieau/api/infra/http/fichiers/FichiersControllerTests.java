package com.github.mtesmct.rieau.api.infra.http.fichiers;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;

import com.github.mtesmct.rieau.api.application.dossiers.UserNotOwnerException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.SaveDossierException;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;
import com.github.mtesmct.rieau.api.infra.application.auth.WithAutreDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FichiersControllerTests {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private FichierService fichierService;
	@Autowired
	private FichierFactory fichierFactory;
	@Autowired
	private DossierFactory dossierFactory;
	@Autowired
	private DossierRepository dossierRepository;
	@Autowired
	private ProjetFactory projetFactory;

	private String uri;
	private Fichier fichier;
	@Autowired
	@Qualifier("deposantBeta")
	private User deposantBeta;
	@Autowired
	@Qualifier("autreDeposantBeta")
	private User autreDeposantBeta;

	@BeforeEach
	public void setup()
			throws IOException, CommuneNotFoundException, StatutForbiddenException, TypeStatutNotFoundException,
			FichierServiceException, PieceNonAJoindreException, TypeDossierNotFoundException, SaveDossierException {
		this.uri = FichiersController.ROOT_URI;
		File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		fichier = this.fichierFactory.creer(file, MediaType.APPLICATION_PDF_VALUE);
		this.fichierService.save(fichier);
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01", new ParcelleCadastrale("0","1","2"), true, true);
        Dossier dp = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet, fichier.identity());
		dp = this.dossierRepository.save(dp);
	}

	@Test
	@WithDeposantBetaDetails
	public void lireDeposantTest() throws Exception {
		this.mvc.perform(get(this.uri + "/" + this.fichier.identity().toString()).accept(MediaType.APPLICATION_PDF,
				MediaType.APPLICATION_JSON, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.IMAGE_GIF))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_PDF))
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@WithMairieBetaDetails
	public void lireMairieTest() throws Exception {
		this.mvc.perform(get(this.uri + "/" + this.fichier.identity().toString()).accept(MediaType.APPLICATION_PDF,
				MediaType.APPLICATION_JSON, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.IMAGE_GIF))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_PDF))
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@WithInstructeurNonBetaDetails
	public void lireInstructeurTest() throws Exception {
		this.mvc.perform(get(this.uri + "/" + this.fichier.identity().toString()).accept(MediaType.APPLICATION_PDF,
				MediaType.APPLICATION_JSON, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.IMAGE_GIF))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_PDF))
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void lireNonAuthentifieTest() throws Exception {
		this.mvc.perform(get(this.uri + "/" + this.fichier.identity().toString()).accept(MediaType.APPLICATION_PDF,
				MediaType.APPLICATION_JSON, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.IMAGE_GIF))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithAutreDeposantBetaDetails
	public void lireNonProprietaireTest() throws Exception {
		this.mvc.perform(get(this.uri + "/" + this.fichier.identity().toString()).accept(MediaType.APPLICATION_PDF,
				MediaType.APPLICATION_JSON, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.IMAGE_GIF))
				.andExpect(status().isForbidden())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.message",
						containsString(UserNotOwnerException.message(this.autreDeposantBeta.identity().toString(), this.fichier.identity().toString()))));
	}

}