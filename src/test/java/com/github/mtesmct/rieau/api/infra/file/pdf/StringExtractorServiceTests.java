package com.github.mtesmct.rieau.api.infra.file.pdf;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StringExtractorServiceTests {
    @Autowired
    private StringExtractService stringExtractService;

    @Test
    public void extractCodeCerfaOKTest() throws DossierImportException {
        String text = "pour une maison individuelle et / ou ses annexes N° 13406*06".trim();
        Optional<String> code = stringExtractService.extract("[\\d]{5}", text, 0);
        assertThat(code.isPresent(), is(true));
        assertThat(code.get(), is("13406"));
    }
    @Test
    public void extractCodeCerfaKOTest() throws DossierImportException {
        Optional<String> code = stringExtractService.extract("[\\d]{5}", "N° E*6 sdgfsdgdfg", 3);
        assertThat(code.isPresent(), is(false));
    }
}