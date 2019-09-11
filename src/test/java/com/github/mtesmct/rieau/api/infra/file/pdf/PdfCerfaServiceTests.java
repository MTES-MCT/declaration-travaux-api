package com.github.mtesmct.rieau.api.infra.file.pdf;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PdfCerfaServiceTests {
    @Autowired
    private PdfCerfaImportService filePdfCerfaService;

    @Test
    public void lireCerfaDPMITest() throws IOException {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        PieceJointe cerfa = this.filePdfCerfaService.lire("jean.martin", file);
        assertThat(cerfa, notNullValue());
        assertThat(cerfa.code(), is("13703"));
    }
    @Test
    public void lireCerfaPCMITest() throws IOException {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        PieceJointe cerfa = this.filePdfCerfaService.lire("jean.martin", file);
        assertThat(cerfa, notNullValue());
        assertThat(cerfa.code(), is("13406"));
    }
}