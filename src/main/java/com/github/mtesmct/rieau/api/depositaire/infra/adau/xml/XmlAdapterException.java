package com.github.mtesmct.rieau.api.depositaire.infra.adau.xml;

public class XmlAdapterException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public XmlAdapterException(String message) {
        super(message);
    }

    public XmlAdapterException(String message, Throwable cause) {
        super(message, cause);
    }
}