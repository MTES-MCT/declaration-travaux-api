package com.github.mtesmct.rieau.api.depositaire.infra.adau;

import java.io.File;
import java.io.IOException;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.services.DepotImportException;
import com.github.mtesmct.rieau.api.depositaire.domain.services.DossierService;
import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.ADAUMessage;
import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.XmlAdapter;
import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.XmlAdapterException;
import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.XmlService;
import com.github.mtesmct.rieau.api.depositaire.infra.file.zip.FileZipService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ADAUFileDossierService implements DossierService {

	@Autowired
	private FileZipService fileZipService;
	@Autowired
	private XmlService xmlService;
	@Autowired
	private XmlAdapter xmlAdapter;
	@Autowired
	private DepotRepository depotRepository;
	@Value("${app.file.zip.dest.dir}")
	private String fileZipDestDir;

	@Override
	public void importerDepot(File file) throws DepotImportException {
		String filePath = file.getAbsolutePath();
		try {
			this.fileZipService.unzip(filePath);
		} catch (IOException e) {
			throw new DepotImportException("impossible de décompresser le fichier zip: " + filePath, e);
		}
		String messageFileName = this.fileZipDestDir + "/message.xml";
		ADAUMessage message = this.xmlService.unmarshall(messageFileName);
		if (message == null)
			throw new DepotImportException(messageFileName + " impossible à unmarshaller");
		Depot depot;
		try {
			depot = this.xmlAdapter.fromXml(message);
		} catch (XmlAdapterException e) {
			throw new DepotImportException("impossible de converir le message XML ADAU en dépôt: " + message.toString(), e);
		}
		if (depot == null)
			throw new DepotImportException(messageFileName + " impossible de convertir en dépôt");
		this.depotRepository.save(depot);
	}

}