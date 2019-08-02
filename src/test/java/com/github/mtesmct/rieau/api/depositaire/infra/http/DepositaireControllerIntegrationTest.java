package com.github.mtesmct.rieau.api.depositaire.infra.http;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Identite;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdentiteRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.depositaire.infra.date.MockDateRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithDepositaireDetails
public class DepositaireControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private IdentiteRepository identiteRepository;
	@Autowired
	private DepotRepository depotRepository;
	
	private MockDateRepository dateRepository;

	private Depositaire depositaire;
	@Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter dateConverter;


	private Depot depot;

	@Before
	public void setup() {
		this.dateRepository = new MockDateRepository(this.dateConverter,"01/01/2019 00:00:00");
        this.depositaire = new Depositaire(this.depotRepository, dateRepository);
		this.identiteRepository.save(new Identite("jean.martin", "Martin", "Jean", "jean.martin@monfai.fr"));
		assertThat(this.identiteRepository.findById("jean.martin").isPresent(), is(true));
		this.depot = new Depot("0", "dp", "instruction", this.dateRepository.now());
		this.depositaire.depose(this.depot);
		assertThat(this.depositaire.listeMesDepots(), not(empty()));
	}

	@Test
	public void listeMesDepotsTest() throws Exception {
		this.mvc.perform(get("/depots").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", equalTo(this.depot.getId())))
				.andExpect(jsonPath("$[0].type", equalTo(this.depot.getType())))
				.andExpect(jsonPath("$[0].etat", equalTo(this.depot.getEtat())))
				.andExpect(jsonPath("$[0].date", equalTo(this.dateConverter.format((this.depot.getDate())))));
	}

	@Test
	@WithMockUser
	public void listeMesDepotsInterditTest() throws Exception {
		this.mvc.perform(get("/depots").accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@Test
	@WithAnonymousUser
	public void listeMesDepotsNonAutoriseTest() throws Exception {
		this.mvc.perform(get("/depots").accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
	}

	@Test
	public void trouveMaDepotTest() throws Exception {
		this.mvc.perform(get("/depots/0").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.depot.getId())))
				.andExpect(jsonPath("$.type", equalTo(this.depot.getType())))
				.andExpect(jsonPath("$.etat", equalTo(this.depot.getEtat())))
				.andExpect(jsonPath("$.date", equalTo(this.dateConverter.format(this.depot.getDate()))));
	}

}