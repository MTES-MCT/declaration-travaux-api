package com.github.mtesmct.rieau.api.depositaire.infra.adau.xml;

public interface XmlService {
    public ADAUMessage unmarshall(String messageFileName) throws XmlUnmarshallException;
}