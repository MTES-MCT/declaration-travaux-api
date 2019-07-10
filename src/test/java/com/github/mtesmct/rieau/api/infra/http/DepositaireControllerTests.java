package com.github.mtesmct.rieau.api.infra.http;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.github.mtesmct.rieau.api.domain.entities.Demande;
import com.github.mtesmct.rieau.api.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.domain.repositories.IdentiteRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithDepositaireDetails
public class DepositaireControllerTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private IdentiteRepository identiteRepository;
	@Autowired
	private DateRepository dateRepository;

	@Autowired
	private Depositaire depositaire;

	private Demande demande;

	@Before
	public void setup() {
		assertThat(this.identiteRepository.findById("jean.martin").isPresent(), is(true));
		this.demande = new Demande("0", "dp", "instruction", this.dateRepository.now());
		this.depositaire.depose(this.demande);
		assertThat(this.depositaire.listeSesDemandes(), not(empty()));
	}

	@Test
	public void listeMesDemandesTest() throws Exception {
		this.mvc.perform(get("/demandes").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", equalTo(this.demande.getId())))
				.andExpect(jsonPath("$[0].type", equalTo(this.demande.getType())))
				.andExpect(jsonPath("$[0].etat", equalTo(this.demande.getEtat())))
				.andExpect(jsonPath("$[0].date", equalTo(this.demande.getDate())));
	}

	@Test
	@WithMockUser
	public void listeMesDemandesInterditTest() throws Exception {
		this.mvc.perform(get("/demandes").accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@Test
	@WithAnonymousUser
	public void listeMesDemandesNonAutoriseTest() throws Exception {
		this.mvc.perform(get("/demandes").accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
	}

	@Test
	public void trouveMaDemandeTest() throws Exception {
		this.mvc.perform(get("/demandes/0").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.demande.getId())))
				.andExpect(jsonPath("$.type", equalTo(this.demande.getType())))
				.andExpect(jsonPath("$.etat", equalTo(this.demande.getEtat())))
				.andExpect(jsonPath("$.date", equalTo(this.demande.getDate())));
	}

}