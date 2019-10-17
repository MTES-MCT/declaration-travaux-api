package com.github.mtesmct.rieau.api.infra.application.dossiers;

import java.io.InputStream;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.AjouterPieceJointeService;
import com.github.mtesmct.rieau.api.application.dossiers.AppAjouterPieceJointeService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TxAjouterPieceJointeService implements AjouterPieceJointeService {

    @Autowired
    private AppAjouterPieceJointeService applicationAjouterPieceJointeService;

    @Override
    public Optional<PieceJointe> execute(DossierId id, String numero, InputStream is, String nom, String mimeType, long taille)
            throws AjouterPieceJointeException, AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        return this.applicationAjouterPieceJointeService.execute(id, numero, is, nom, mimeType, taille);
    }

}