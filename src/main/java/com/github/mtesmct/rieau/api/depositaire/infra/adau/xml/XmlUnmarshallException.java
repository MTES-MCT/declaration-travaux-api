package com.github.mtesmct.rieau.api.depositaire.infra.adau.xml;

public class XmlUnmarshallException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public XmlUnmarshallException(String message) {
        super(message);
    }

    public XmlUnmarshallException(String message, Throwable cause) {
        super(message, cause);
    }
}