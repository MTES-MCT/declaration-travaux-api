package com.github.mtesmct.rieau.api.infra.application.dossiers;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.AppPrendreDecisionDossierService;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.PrendreDecisionDossierService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TxPrendreDecisionDossierService implements PrendreDecisionDossierService {

    @Autowired
    private AppPrendreDecisionDossierService applicationPrendreDecisionDossierService;

    @Override
    public Optional<Dossier> execute(DossierId id, InputStream is, String nom, String mimeType, long taille)
            throws DossierNotFoundException, MairieForbiddenException, AuthRequiredException, UserForbiddenException,
            UserInfoServiceException, TypeStatutNotFoundException, StatutForbiddenException, FileNotFoundException,
            AjouterPieceJointeException {
        return this.applicationPrendreDecisionDossierService.execute(id, is, nom, mimeType, taille);
    }
}