package com.github.mtesmct.rieau.api.infra.file.pdf;

import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportException;
import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfCerfaImportService implements CerfaImportService {

	@Autowired
	private StringExtractService stringExtractService;

	@Override
	public Optional<String> lireCode(Fichier fichier) throws CerfaImportException {
		PDDocument doc = null;
		Optional<String> code = Optional.empty();
		String pdfMimeType = "application/pdf";
		if (!fichier.mimeType().equals(pdfMimeType))
			throw new CerfaImportException("Le type MIME du fichier n'est pas " + pdfMimeType);
		try {
			doc = PDDocument.load(fichier.content(), MemoryUsageSetting.setupTempFileOnly());
			if (doc.isEncrypted()) {
				throw new CerfaImportException("Le pdf est chiffré");
			}
			AccessPermission ap = doc.getCurrentAccessPermission();
			if (!ap.canExtractContent()) {
				throw new CerfaImportException("Pas de permission d'extraire du texte depuis le pdf");
			}
			log.debug("Le pdf contient " + doc.getNumberOfPages() + " pages");
			if (doc.getNumberOfPages() < 2) {
				throw new CerfaImportException("Le pdf contient moins de 2 pages");
			}
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setStartPage(2);
			stripper.setEndPage(2);
			stripper.setSortByPosition(true);
			stripper.setLineSeparator("\n");
			String[] lines = stripper.getText(doc).split(stripper.getLineSeparator());
			log.debug("{} lignes de texte", lines.length);
			for (String line : lines) {
				log.trace("line={}", line);
				Optional<String> lCode = this.stringExtractService.extract("[\\d]{5}", line, 0);
				if (lCode.isPresent())
					code = lCode;
			}
			doc.close();
		} catch (IOException e) {
			throw new CerfaImportException("Erreur de chargement du pdf: " + fichier.nom(), e);
		}
		return code;
	}

}