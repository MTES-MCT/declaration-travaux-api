package com.github.mtesmct.rieau.api.infra.http;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
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

import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.file.upload.FileUploadService;

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
@SpringBootTest
@AutoConfigureMockMvc
@WithDeposantAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DossiersControllerTests {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private DossierRepository dossierRepository;
	@Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter dateConverter;
    @Autowired
    private DossierFactory dossierFactory;
    @MockBean
    private FileUploadService fileUploadService;

	private Dossier dossier;

	private String uri;

	@Autowired
	@Qualifier("deposantBeta")
	private PersonnePhysique deposantBeta;

	@Before
	public void setup() {
		this.uri = DossiersController.ROOT_URL;
		File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		PieceJointe cerfa = new PieceJointe(new CodePieceJointe(TypePieceJointe.CERFA, null), file);
		this.dossier = this.dossierFactory.creer(this.deposantBeta, cerfa, TypeDossier.DP);
        this.dossier = this.dossierRepository.save(this.dossier);
		assertThat(this.dossier.identity(), notNullValue());
		assertThat(this.dossier.deposant().identity().toString(), is(equalTo(this.deposantBeta.identity().toString())));
	}

	@Test
	public void listerTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", equalTo(this.dossier.identity().toString())))
				.andExpect(jsonPath("$[0].type", equalTo(this.dossier.type().toString())))
				.andExpect(jsonPath("$[0].statut", equalTo(this.dossier.statut().toString())))
				.andExpect(jsonPath("$[0].date", equalTo(this.dateConverter.format((this.dossier.dateDepot())))));
	}

	@Test
	@WithMockUser
	public void listerInterditTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@Test
	@WithAnonymousUser
	public void listerRedirigeConnexionTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sso/login"));
	}
	
	@Test
	public void consulterTest() throws Exception {
		this.mvc.perform(get(this.uri+"/"+this.dossier.identity().toString()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.dossier.identity().toString())))
				.andExpect(jsonPath("$.type", equalTo(this.dossier.type().toString())))
				.andExpect(jsonPath("$.statut", equalTo(this.dossier.statut().toString())))
				.andExpect(jsonPath("$.date", equalTo(this.dateConverter.format(this.dossier.dateDepot()))));
	}

	@Test
	public void ajouterAutoriseTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf",
		"application/pdf", "Spring Framework".getBytes());
		Mockito.when(this.fileUploadService.store(anyString(), any())).thenReturn(new File("src/test/fixtures/cerfa_13406_PCMI.pdf"));
		this.mvc.perform(multipart(this.uri).file(multipartFile).with(csrf().asHeader())).andExpect(status().isOk());
		assertThat(this.dossierRepository.findByDeposantId(this.deposantBeta.identity().toString()).size(), equalTo(2));
	}

	@Test
	public void ajouterSansCsrfInterditTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf",
		"application/pdf", "Spring Framework".getBytes());
		this.mvc.perform(multipart(this.uri).file(multipartFile)).andExpect(status().isForbidden());
	}

	@Test
	@WithInstructeurNonBetaDetails
	public void ajouterInterditTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf",
		"application/pdf", "Spring Framework".getBytes());
		this.mvc.perform(multipart(this.uri).file(multipartFile).with(csrf().asHeader())).andExpect(status().isForbidden());
	}


}