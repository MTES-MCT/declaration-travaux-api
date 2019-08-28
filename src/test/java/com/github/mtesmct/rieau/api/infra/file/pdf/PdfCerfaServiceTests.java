package com.github.mtesmct.rieau.api.infra.file.pdf;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import com.github.mtesmct.rieau.api.domain.entities.Cerfa;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PdfCerfaServiceTests {
    @Autowired
    private PdfCerfaService filePdfCerfaService;

    @Test
    public void lireCerfaDPMITest() throws IOException {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        Cerfa cerfa = this.filePdfCerfaService.lire(file);
        assertThat(cerfa, notNullValue());
        assertThat(cerfa.getCode(), is("N° 13703*06"));
    }
    @Test
    public void lireCerfaPCMITest() throws IOException {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        Cerfa cerfa = this.filePdfCerfaService.lire(file);
        assertThat(cerfa, notNullValue());
        assertThat(cerfa.getCode(), is("N° 13406*06"));
    }
}