package com.github.mtesmct.rieau.api.application.dossiers;

public class CerfaImportException extends Exception {
    private static final long serialVersionUID = 1L;
	public final static String MOINS_2_PAGES = "Le pdf du CERFA doit contenir au moins 2 pages, car le code CERFA est recherché sur la 2ème page";

    public CerfaImportException(String message) {
        super(message);
    }

    public CerfaImportException(String message, Throwable cause) {
        super(message, cause);
    }
}