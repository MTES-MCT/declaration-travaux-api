package com.github.mtesmct.rieau.api.infra.file.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.CerfaImportService;
import com.github.mtesmct.rieau.api.application.DossierImportException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class PdfCerfaImportService implements CerfaImportService {

	@Override
	public PieceJointe lire(String demandeur, File file) throws DossierImportException {
		PDDocument doc;
		PieceJointe cerfa = null;
		try {
			doc = PDDocument.load(file, MemoryUsageSetting.setupTempFileOnly());
			AccessPermission ap = doc.getCurrentAccessPermission();
			if (!ap.canExtractContent()) {
				throw new IOException("Pas de permission d'extraire du texte depuis le pdf");
			}
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setStartPage(2);
			stripper.setEndPage(2);
			String pageText = stripper.getText(doc);
			List<String> codes = Arrays.asList("N° 13406*06", "N° 13703*06");
			Optional<String> code = codes.stream().filter(c -> pageText.contains(c)).findAny();
			if (code.isEmpty())
				throw new DossierImportException("Pas de code CERFA reconnu dans le texte du pdf");
			
			doc.close();
		} catch (IOException e) {
			throw new DossierImportException("Erreur de chargement du pdf: " + file.getPath(), e);
		}
		return cerfa;
	}

}