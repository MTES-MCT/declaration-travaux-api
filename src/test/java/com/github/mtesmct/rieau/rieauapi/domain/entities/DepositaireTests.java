package com.github.mtesmct.rieau.rieauapi.domain.entities;

import static org.junit.Assert.assertThat;

import com.github.mtesmct.rieau.rieauapi.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.rieauapi.infra.http.WithDepositaireDetails;
import com.github.mtesmct.rieau.rieauapi.infra.repositories.fakedate.FakeDateRepository;
import com.github.mtesmct.rieau.rieauapi.infra.repositories.inmemory.InMemoryDepositaireRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@RunWith(SpringRunner.class)
@SpringBootTest
@WithDepositaireDetails
@ActiveProfiles("test")
public class DepositaireTests {

    private Depositaire depositaire;
    private String date;
    private DateRepository fakeDateRepository;

    @Before
    public void setUp() throws Exception {
        this.date = "01/01/2019 00:00:00";
        this.fakeDateRepository = new FakeDateRepository(date);
        this.depositaire =  new Depositaire(new InMemoryDepositaireRepository(), fakeDateRepository);
    }

    @Test
    @WithDepositaireDetails
    public void depose() {
        Demande demande = new Demande("0", "dp", "instruction");
        this.depositaire.depose(demande);
        assertThat(this.depositaire.listeSesDemandes(), not(empty()));
        assertThat(this.depositaire.listeSesDemandes().size(), is(1));
        assertThat(this.depositaire.listeSesDemandes().get(0), notNullValue());
        assertThat(this.depositaire.listeSesDemandes().get(0).getEtat(), is(demande.getEtat()));
        assertThat(this.depositaire.listeSesDemandes().get(0).getDate().compareTo(this.fakeDateRepository.now()), is(0));
        assertThat(this.depositaire.trouveMaDemande(demande.getId()).isPresent(), is(true));
        assertThat(this.depositaire.trouveMaDemande(demande.getId()).get(), equalTo(this.depositaire.listeSesDemandes().get(0)));
    }

}