package com.github.mtesmct.rieau.api.depositaire.infra.file.pdf;

import java.io.File;
import java.io.IOException;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Cerfa;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.DepotImportException;
import com.github.mtesmct.rieau.api.depositaire.domain.services.CerfaService;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.springframework.stereotype.Service;

@Service
public class FilePdfCerfaService implements CerfaService {

	@Override
	public Cerfa lireCerfa(File file) throws DepotImportException {
		PDDocument doc;
		Cerfa cerfa = null;
		try {
			doc = PDDocument.load(file, MemoryUsageSetting.setupTempFileOnly());
			AccessPermission ap = doc.getCurrentAccessPermission();
			if (!ap.canExtractContent()) {
				throw new IOException("Pas de permission d'extraire du texte depuis le pdf");
			}
			PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
			if (acroForm != null) {
				//TODO
			}
			doc.close();
		} catch (IOException e) {
			throw new DepotImportException("Erreur de chargement du pdf: " + file.getPath(), e);
		}
		return cerfa;
	}

}