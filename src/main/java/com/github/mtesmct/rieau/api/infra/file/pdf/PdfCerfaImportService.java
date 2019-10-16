package com.github.mtesmct.rieau.api.infra.file.pdf;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportException;
import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportService;
import com.github.mtesmct.rieau.api.application.dossiers.CodeCerfaNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.NombrePagesCerfaException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
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
	@Autowired
	private TypeDossierRepository typeDossierRepository;

	@Override
	public Map<String, String> lire(Fichier fichier) throws CerfaImportException {
		Map<String, String> valeurs = new HashMap<String, String>();
		Optional<TypeDossier> type = Optional.empty();
		PDDocument doc = null;
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
			PDFTextStripper stripper = new PDFTextStripper();
			int nombrePagesMini = 2;
			if (doc.getNumberOfPages() < nombrePagesMini)
				throw new CerfaImportException(new NombrePagesCerfaException(nombrePagesMini));
			stripper.setStartPage(nombrePagesMini);
			stripper.setEndPage(nombrePagesMini);
			stripper.setSortByPosition(true);
			stripper.setLineSeparator("\n");
			String[] lines = stripper.getText(doc).split(stripper.getLineSeparator());
			log.debug("{} lignes de texte", lines.length);
			for (String line : lines) {
				log.trace("line={}", line);
				Optional<String> lCode = this.stringExtractService.extract("[\\d]{5}", line, 0);
				if (lCode.isPresent()) {
					type = this.typeDossierRepository.findByCode(lCode.get());
					if (type.isEmpty())
						throw new CerfaImportException(new CodeCerfaNotFoundException());
					valeurs.put("type", type.get().type().toString());
				}
			}
			PDDocumentCatalog docCatalog = doc.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			for (String attribut : this.cerfaFormMapper.nomsChamps(type.get().type())) {
				PDField field = acroForm.getField(this.cerfaFormMapper.toNomChamp(type.get().type(), attribut));
				String valeur = field.getValueAsString();
				log.debug("La valeur trouvée de l'attribut {} est {}", attribut, valeur);
				if (Arrays.asList(new String[]{"nouvelleConstruction","lotissement"}).contains(attribut)) {
					if (valeur != null && Arrays.asList(new String[] { "On", "Oui", "true", "TRUE", "1", "on", "oui" })
							.contains(valeur)) {
						valeur = "true";
					} else {
						valeur = "false";
					}
				}
				valeurs.put(attribut, valeur);
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
		return valeurs;
	}

	@Override
	public Set<String> keys(EnumTypes type) {
		return this.cerfaFormMapper.nomsChamps(type);
	}

}