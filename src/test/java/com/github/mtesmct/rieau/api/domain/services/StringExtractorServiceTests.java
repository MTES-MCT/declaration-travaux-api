package com.github.mtesmct.rieau.api.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.domain.services.StringExtractService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StringExtractorServiceTests {
    @Autowired
    private StringExtractService stringExtractService;

    @Test
    public void extractCodeCerfaOKTest() throws DossierImportException {
        String text = "pour une maison individuelle et / ou ses annexes N° 13406*06".trim();
        Optional<String> code = stringExtractService.extract("[\\d]{5}", text, 0);
        assertTrue(code.isPresent());
        assertEquals(code.get(), "13406");
    }
    @Test
    public void extractCodeCerfaKOTest() throws DossierImportException {
        Optional<String> code = stringExtractService.extract("[\\d]{5}", "N° E*6 sdgfsdgdfg", 3);
        assertFalse(code.isPresent());
    }
}