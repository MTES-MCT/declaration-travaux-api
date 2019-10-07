package com.github.mtesmct.rieau.api.infra.file.pdf;

import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportException;
import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportService;
import com.github.mtesmct.rieau.api.application.dossiers.NombrePagesCerfaException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.services.StringExtractService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfCerfaImportService implements CerfaImportService {

	@Autowired
	private StringExtractService stringExtractService;
	@Autowired
	private CerfaFormMapper cerfaFormMapper;
	

	@Override
	public Optional<String> lireCode(Fichier fichier) throws CerfaImportException {
		PDDocument doc = null;
		Optional<String> code = Optional.empty();
		String pdfMimeType = "application/pdf";
		if (!fichier.mimeType().equals(pdfMimeType))
			throw new CerfaImportException("Le type MIME du fichier n'est pas {" + pdfMimeType + "}");
		try {
			doc = PDDocument.load(fichier.contenu());
			if (doc.isEncrypted()) {
				throw new CerfaImportException("Le pdf est chiffré");
			}
			AccessPermission ap = doc.getCurrentAccessPermission();
			if (!ap.canExtractContent()) {
				throw new CerfaImportException("Pas de permission d'extraire du texte depuis le pdf");
			}
			log.debug("Le pdf contient " + doc.getNumberOfPages() + " pages");
			int nombrePagesMini = 2;
			if (doc.getNumberOfPages() < nombrePagesMini) {
				throw new CerfaImportException(new NombrePagesCerfaException(nombrePagesMini));
			}
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setStartPage(nombrePagesMini);
			stripper.setEndPage(nombrePagesMini);
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
		} catch (IOException e) {
			throw new CerfaImportException("Erreur de chargement du pdf {" + fichier.nom() + "}", e);
		} finally {
			if (doc != null)
				try {
					doc.close();
				} catch (IOException e) {
					throw new CerfaImportException("Erreur de chargement du pdf {" + fichier.nom() + "}", e);
				}
		}
		return code;
	}

	@Override
	public Optional<String> lireFormValue(Fichier fichier, TypesDossier type, String attribut) throws CerfaImportException {
		PDDocument doc = null;
		Optional<String> valeur = Optional.empty();
		String pdfMimeType = "application/pdf";
		if (!fichier.mimeType().equals(pdfMimeType))
			throw new CerfaImportException("Le type MIME du fichier n'est pas {" + pdfMimeType + "}");
		try {
			doc = PDDocument.load(fichier.contenu());
			if (doc.isEncrypted()) {
				throw new CerfaImportException("Le pdf est chiffré");
			}
			AccessPermission ap = doc.getCurrentAccessPermission();
			if (!ap.canExtractContent()) {
				throw new CerfaImportException("Pas de permission d'extraire du texte depuis le pdf");
			}
			log.debug("Le pdf contient " + doc.getNumberOfPages() + " pages");
			int nombrePagesMini = 3;
			if (doc.getNumberOfPages() < nombrePagesMini) {
				throw new CerfaImportException(new NombrePagesCerfaException(nombrePagesMini));
			}
			PDDocumentCatalog docCatalog = doc.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
	        PDField field = acroForm.getField(this.cerfaFormMapper.toNomChamp(type, attribut));
			valeur = Optional.ofNullable(field.getValueAsString());
		} catch (IOException e) {
			throw new CerfaImportException("Erreur de chargement du pdf {" + fichier.nom() + "}", e);
		} finally {
			if (doc != null)
				try {
					doc.close();
				} catch (IOException e) {
					throw new CerfaImportException("Erreur de chargement du pdf {" + fichier.nom() + "}", e);
				}
		}
		return valeur;
	}

}