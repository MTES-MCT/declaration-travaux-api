package com.github.mtesmct.rieau.api.application.dossiers;

public class CerfaImportException extends Exception {
    private static final long serialVersionUID = 1L;
	public final static String MOINS_2_PAGES = "Le pdf contient moins de 2 pages";

    public CerfaImportException(String message) {
        super(message);
    }

    public CerfaImportException(String message, Throwable cause) {
        super(message, cause);
    }
}