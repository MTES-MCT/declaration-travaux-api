package com.github.mtesmct.rieau.api.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.infra.file.pdf.PdfCerfaImportService;

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
        String text = "pour une maison individuelle et / ou ses annexes N° 13406*06";
        Optional<String> code = stringExtractService.extract(PdfCerfaImportService.CODE_CERFA_REGEXP, text, 1);
        assertTrue(code.isPresent());
        assertEquals("13406", code.get());
    }
    @Test
    public void extractCodeCerfaKOTest() throws DossierImportException {
        Optional<String> code = stringExtractService.extract(PdfCerfaImportService.CODE_CERFA_REGEXP, "N° E*6 sdgfsdgdfg", 3);
        assertTrue(code.isEmpty());
    }
    @Test
    public void extractCommuneStringTest() throws DossierImportException {
        String text = "Commune={ codePostal={codePostal}, nom={nom}, departement={departement} }";
        Optional<String> communeString = stringExtractService.extract(this.stringExtractService.entityRegexp("Commune"), text, 1);
        assertTrue(communeString.isPresent());
        assertEquals("codePostal={codePostal}, nom={nom}, departement={departement}", communeString.get().trim());
    }
    @Test
    public void extractCodePostalStringTest() throws DossierImportException {
        String text = "Commune={ codePostal={codePostal}, nom={nom}, departement={departement} }";
        Optional<String> codePostalString = stringExtractService.extract(this.stringExtractService.attributeRegexp("codePostal"), text, 1);
        assertTrue(codePostalString.isPresent());
        assertEquals("codePostal", codePostalString.get());
    }
    @Test
    public void extractNomStringTest() throws DossierImportException {
        String text = "Commune={ codePostal={codePostal}, nom={nom}, departement={departement} }";
        Optional<String> nomString = stringExtractService.extract(this.stringExtractService.attributeRegexp("nom"), text, 1);
        assertTrue(nomString.isPresent());
        assertEquals("nom", nomString.get());
    }
}