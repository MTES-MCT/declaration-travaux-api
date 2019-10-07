package com.github.mtesmct.rieau.api.application.dossiers;

public class NombrePagesCerfaException extends Exception {
    private static final long serialVersionUID = 1L;

    public final static String nombrePagesInsuffisant(int nombre) {
        return "Le pdf du CERFA doit contenir au moins " + nombre
                + " pages, car le code CERFA est recherché sur la 2ème page";
    }

    public NombrePagesCerfaException(int nombre) {
        super(nombrePagesInsuffisant(nombre));
    }

    public NombrePagesCerfaException(int nombre, Throwable cause) {
        super(nombrePagesInsuffisant(nombre), cause);
    }
}