package com.github.mtesmct.rieau.api.infra.http;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import com.github.mtesmct.rieau.api.application.dossiers.ListerMesDossiersService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysiqueId;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.file.upload.FileUploadService;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonnePhysiqueFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories.JpaSpringPersonnePhysiqueRepository;
import com.github.mtesmct.rieau.api.infra.security.WithDemandeurAndBetaDetails;
import com.github.mtesmct.rieau.api.infra.security.WithInstructeurNonBetaDetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"app.datetime.mock=01/01/2019 00:00:00"})
@AutoConfigureMockMvc
@WithDemandeurAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DossiersControllerTests {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private DossierRepository dossierRepository;
	@Autowired
	private ListerMesDossiersService listerMesDossiersService;
	@Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter dateConverter;
    @Autowired
    private DossierFactory dossierFactory;
    @MockBean
    private FileUploadService mockFileUploadService;

	@Autowired
	private JpaPersonnePhysiqueFactory jpaPersonnePhysiqueFactory;

	@Autowired
	private JpaSpringPersonnePhysiqueRepository jpaSpringPersonnePhysiqueRepository;


	private Dossier dossier;

	private String uri;

	@Before
	public void setup() {
		this.uri = DossiersController.ROOT_URL;
		PersonnePhysique demandeur = new PersonnePhysique(new PersonnePhysiqueId("insee_01"), "jean.martin@monfai.fr", "Martin", "Jean", Sexe.HOMME, new Naissance(this.dateConverter.parse("01/01/1900 00:00:00"), "44000"));
		this.jpaSpringPersonnePhysiqueRepository.save(this.jpaPersonnePhysiqueFactory.toJpa(demandeur));
		PieceJointe cerfa = new PieceJointe(new CodePieceJointe(TypePieceJointe.CERFA, null), new File("test"));
		this.dossier = this.dossierFactory.creer(demandeur);
        this.dossierRepository.save(this.dossier);
	}

	@Test
	public void listeMesDossiersTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", equalTo(this.dossier.identity())))
				.andExpect(jsonPath("$[0].type", equalTo(this.dossier.cerfa().code().type().toString())))
				.andExpect(jsonPath("$[0].etat", equalTo(this.dossier.statut().toString())))
				.andExpect(jsonPath("$[0].date", equalTo(this.dateConverter.format((this.dossier.dateDepot())))));
	}

	@Test
	@WithMockUser
	public void listeInterditTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@Test
	@WithAnonymousUser
	public void listeRedirigeConnexionTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sso/login"));
	}
	
	@Test
	public void donneTest() throws Exception {
		this.mvc.perform(get(this.uri+"/"+this.dossier.identity()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.dossier.identity())))
				.andExpect(jsonPath("$.type", equalTo(this.dossier.cerfa().code().type().toString())))
				.andExpect(jsonPath("$.etat", equalTo(this.dossier.statut().toString())))
				.andExpect(jsonPath("$.date", equalTo(this.dateConverter.format(this.dossier.dateDepot()))));
	}

	@Test
	public void ajouteAutoriseTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.zip",
		"application/zip", "Spring Framework".getBytes());
		Mockito.when(this.mockFileUploadService.store(anyString(), any())).thenReturn(new File("src/test/fixtures/cerfa_13406_PCMI.pdf"));
		this.mvc.perform(multipart(this.uri).file(multipartFile).with(csrf().asHeader())).andExpect(status().isOk());
		assertThat(this.listerMesDossiersService.execute().size(), equalTo(2));
	}

	@Test
	public void ajouteSansCsrfInterditTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.zip",
		"application/zip", "Spring Framework".getBytes());
		this.mvc.perform(multipart(this.uri).file(multipartFile)).andExpect(status().isForbidden());
	}

	@Test
	@WithInstructeurNonBetaDetails
	public void ajouteInterditTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.zip",
		"application/zip", "Spring Framework".getBytes());
		this.mvc.perform(multipart(this.uri).file(multipartFile).with(csrf().asHeader())).andExpect(status().isForbidden());
	}


}