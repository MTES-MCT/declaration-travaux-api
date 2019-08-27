package com.github.mtesmct.rieau.api.depositaire.infra.http;

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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Identite;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.services.NoNationalService;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdentiteRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.depositaire.infra.date.MockDateRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.file.upload.FileUploadService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithDepositaireAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DepositaireControllerTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private IdentiteRepository identiteRepository;
	@Autowired
	private DepotRepository depotRepository;
	
	private MockDateRepository dateRepository;

	@Autowired
	private Depositaire depositaire;
	@Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter dateConverter;
    @Autowired
    private NoNationalService noNationalService;
    @MockBean
    private FileUploadService mockFileUploadService;


	private Depot depot;

	private String uri;

	@Before
	public void setup() {
		this.uri = DepositaireController.ROOT_URL;
		this.dateRepository = new MockDateRepository(this.dateConverter,"01/01/2019 00:00:00");
		this.identiteRepository.save(new Identite("jean.martin", "Martin", "Jean", "jean.martin@monfai.fr"));
		assertThat(this.identiteRepository.findById("jean.martin").isPresent(), is(true));
        this.depot = new Depot(this.noNationalService.getNew(), Type.dp, this.dateRepository.now());
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
	public void listeMesDepotsInterditTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@Test
	@WithAnonymousUser
	public void listeMesDepotsNonAutoriseTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sso/login"));
	}
	
	@Test
	public void trouveMonDepotTest() throws Exception {
		this.mvc.perform(get(this.uri+"/"+this.depot.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.depot.getId())))
				.andExpect(jsonPath("$.type", equalTo(this.depot.getType().toString())))
				.andExpect(jsonPath("$.etat", equalTo(this.depot.getEtat().toString())))
				.andExpect(jsonPath("$.date", equalTo(this.dateConverter.format(this.depot.getDate()))));
	}

	@Test
	public void ajouteDepotAllowedTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.zip",
		"application/zip", "Spring Framework".getBytes());
		Mockito.when(this.mockFileUploadService.store(anyString(), any())).thenReturn(new File("src/test/fixtures/RitaGS.2019-04-03T16_26_40.674661355.A-9-X3UGO4V7-DAUA-2.ADER.ftp.zip"));
		this.mvc.perform(multipart(this.uri).file(multipartFile).with(csrf().asHeader())).andExpect(status().isOk());
		assertThat(this.depositaire.listeMesDepots().size(), equalTo(2));
	}

	@Test
	public void ajouteDepotNoCsrfNotAllowedTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.zip",
		"application/zip", "Spring Framework".getBytes());
		this.mvc.perform(multipart(this.uri).file(multipartFile)).andExpect(status().isForbidden());
	}

	@Test
	@WithInstructeurNonBetaDetails
	public void ajouteDepotForbiddenTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.zip",
		"application/zip", "Spring Framework".getBytes());
		this.mvc.perform(multipart(this.uri).file(multipartFile).with(csrf().asHeader())).andExpect(status().isForbidden());
	}


}