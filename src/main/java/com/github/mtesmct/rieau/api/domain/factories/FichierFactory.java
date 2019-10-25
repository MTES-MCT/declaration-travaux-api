package com.github.mtesmct.rieau.api.domain.factories;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.services.FichierIdService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Factory
public class FichierFactory {
    private FichierIdService fichierIdService;

    public FichierFactory(FichierIdService fichierIdService) {
        if (fichierIdService == null)
            throw new NullPointerException("Le service des id des fichiers ne peut pas être nul.");
        this.fichierIdService = fichierIdService;
    }

    public Fichier creer(File file, String mimeType) throws FileNotFoundException {
        return new Fichier(this.fichierIdService.creer(), file.getName(), mimeType, new FileInputStream(file),
                file.length());
    }

    public Fichier creer(InputStream is, String nom, String mimeType, long taille) throws FileNotFoundException {
        return new Fichier(this.fichierIdService.creer(), nom, mimeType, is, taille);
    }
}