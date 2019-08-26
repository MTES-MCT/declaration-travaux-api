package com.github.mtesmct.rieau.api.depositaire.infra.adau;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.services.DepotImportException;
import com.github.mtesmct.rieau.api.depositaire.domain.services.DossierService;
import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.ADAUMessage;
import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.XmlService;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.depositaire.infra.file.zip.FileZipService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ADAUFileDossierService implements DossierService {

	@Autowired
	private FileZipService fileZipService;
	@Autowired
	private XmlService xmlService;
	@Autowired
	private DepotRepository depotRepository;
	@Value("${app.file.zip.dest.dir}")
	private String fileZipDestDir;
	@Autowired
	@Qualifier("adauDateTimeConverter")
	private DateConverter dateConverter;

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
		Type type = null;
		String code = message.getCode();
		switch (code) {
		case "cerfa_13404_DP":
			type = Type.dp;
		case "cerfa_13703_DPMI":
			type = Type.pcmi;
		}
		if (type == null)
			throw new DepotImportException("Le type du depot est inconnu: " + code
					+ ". Les seuls types acceptés sont cerfa_13404_DP ou cerfa_13703_DPMI.");
		String id = message.getId();
		if (StringUtils.isEmpty(id))
			throw new DepotImportException("L'id du depot ne peut pas être vide.");
		String sDate = message.getDate();
		Date date = this.dateConverter.parse(sDate);
		if (date == null)
			throw new DepotImportException("La date du depot ne peut pas être vide.");
		this.depotRepository.save(new Depot(id, type, date));
	}

}