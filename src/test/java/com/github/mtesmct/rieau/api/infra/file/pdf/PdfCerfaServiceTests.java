package com.github.mtesmct.rieau.api.infra.file.pdf;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Optional;

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
    public void lireCerfaDPMITest() throws Exception {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        assertThat(file.exists(), is(true));
        Optional<String> code = this.filePdfCerfaService.lireCode(file);
        assertThat(code.isPresent(), is(true));
        assertThat(code.get(), is("13703"));
    }
    @Test
    public void lireCerfaPCMITest() throws Exception {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        assertThat(file.exists(), is(true));
        Optional<String> code = this.filePdfCerfaService.lireCode(file);
        assertThat(code.isPresent(), is(true));
        assertThat(code.get(), is("13406"));
    }
}