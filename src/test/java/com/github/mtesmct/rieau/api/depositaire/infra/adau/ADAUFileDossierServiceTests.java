package com.github.mtesmct.rieau.api.depositaire.infra.adau;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.services.DepotImportException;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ADAUFileDossierServiceTests {
    @Autowired
    private ADAUFileDossierService adauFileDossierService;
	@Autowired
	private DepotRepository depotRepository;
    @Qualifier("adauDateTimeConverter")
	@Autowired
    private DateConverter dateConverter;

    @Test
    public void importDepotTest() throws DepotImportException {
        File file = new File("src/test/fixtures/RitaGS.2019-04-03T16_26_40.674661355.A-9-X3UGO4V7-DAUA-2.ADER.ftp.zip");
        this.adauFileDossierService.importerDepot(file);
        List<Depot> depots = this.depotRepository.findAll();
        assertThat(depots.isEmpty(), not(true));
        assertThat(depots.size(), is(1));
        Depot depot = depots.get(0);
        assertThat(depot.getId(), is("A-9-X3UGO4V7"));
        assertThat(depot.getType(), is(Type.dp));
        assertThat(this.dateConverter.format(depot.getDate()), is("2019-04-03T16:26:20.790+02:00"));
    }
}