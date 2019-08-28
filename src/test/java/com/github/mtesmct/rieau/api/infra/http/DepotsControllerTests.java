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

import com.github.mtesmct.rieau.api.application.DepositaireService;
import com.github.mtesmct.rieau.api.application.NoNationalService;
import com.github.mtesmct.rieau.api.domain.entities.Depot;
import com.github.mtesmct.rieau.api.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.date.MockDateRepository;
import com.github.mtesmct.rieau.api.infra.file.upload.FileUploadService;
import com.github.mtesmct.rieau.api.infra.security.WithDepositaireAndBetaDetails;
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
@SpringBootTest
@AutoConfigureMockMvc
@WithDepositaireAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DepotsControllerTests {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private DepotRepository depotRepository;
	
	private MockDateRepository dateRepository;

	@Autowired
	private DepositaireService depositaireService;
	@Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter dateConverter;
    @Autowired
    private NoNationalService noNationalService;
    @MockBean
    private FileUploadService mockFileUploadService;


	private Depot depot;

	private String uri;
    private String depositaire;

	@Before
	public void setup() {
		this.uri = DepotsController.ROOT_URL;
		this.depositaire = "jean.martin";
		this.dateRepository = new MockDateRepository(this.dateConverter,"01/01/2019 00:00:00");
        this.depot = new Depot(this.noNationalService.getNew(), Type.dp, this.dateRepository.now(), this.depositaire);
        this.depotRepository.save(this.depot);
	}

	@Test
	public void listeMesDepotsTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", equalTo(this.depot.getId())))
				.andExpect(jsonPath("$[0].type", equalTo(this.depot.getType().toString())))
				.andExpect(jsonPath("$[0].etat", equalTo(this.depot.getEtat().toString())))
				.andExpect(jsonPath("$[0].date", equalTo(this.dateConverter.format((this.depot.getDate())))));
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
		this.mvc.perform(get(this.uri+"/"+this.depot.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.depot.getId())))
				.andExpect(jsonPath("$.type", equalTo(this.depot.getType().toString())))
				.andExpect(jsonPath("$.etat", equalTo(this.depot.getEtat().toString())))
				.andExpect(jsonPath("$.date", equalTo(this.dateConverter.format(this.depot.getDate()))));
	}

	@Test
	public void ajouteAutoriseTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.zip",
		"application/zip", "Spring Framework".getBytes());
		Mockito.when(this.mockFileUploadService.store(anyString(), any())).thenReturn(new File("src/test/fixtures/cerfa_13406_PCMI.pdf"));
		this.mvc.perform(multipart(this.uri).file(multipartFile).with(csrf().asHeader())).andExpect(status().isOk());
		assertThat(this.depositaireService.liste(this.depositaire).size(), equalTo(2));
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