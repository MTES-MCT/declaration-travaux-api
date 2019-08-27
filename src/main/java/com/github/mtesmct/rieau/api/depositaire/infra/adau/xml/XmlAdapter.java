package com.github.mtesmct.rieau.api.depositaire.infra.adau.xml;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;
import org.springframework.util.StringUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class XmlAdapter {
	@Autowired
	@Qualifier("adauDateTimeConverter")
    private DateConverter dateConverter;
    
    public Depot fromXml(ADAUMessage message) throws XmlAdapterException {
		Type type = null;
		String code = message.getCode();
		Map<String, Type> codesTypes = new HashMap<String, Type>();
		codesTypes.put("cerfa_13703_DPMI", Type.dp);
		codesTypes.put("cerfa_13406_PCMI", Type.pcmi);
		type = codesTypes.get(code);
		String acceptedCodes = codesTypes.keySet().stream().map(k -> k.toString())
				.collect(Collectors.joining(", ", "[", "]"));
		if (type == null)
			throw new XmlAdapterException(
					"Le type du depot est inconnu: " + code + ". Les seuls types acceptés sont: " + acceptedCodes);
		String id = message.getId();
		if (StringUtils.isEmpty(id))
			throw new XmlAdapterException("L'id du depot ne peut pas être vide.");
		String sDate = message.getDate();
		Date date = this.dateConverter.parse(sDate);
		if (date == null)
			throw new XmlAdapterException("La date du depot ne peut pas être vide.");
		return new Depot(id, type, date);
    }
}