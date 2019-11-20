package com.github.mtesmct.rieau.api.infra.application.fichiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.FichierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.UserNotOwnerException;
import com.github.mtesmct.rieau.api.application.fichiers.AppLireFichierService;
import com.github.mtesmct.rieau.api.application.fichiers.LireFichierService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.InstructeurForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TxLireFichierService implements LireFichierService {

    @Autowired
    private AppLireFichierService lireFichierService;

    @Override
    public Optional<Fichier> execute(FichierId id)
            throws FichierNotFoundException, UserForbiddenException, AuthRequiredException, UserInfoServiceException,
            UserNotOwnerException, DossierNotFoundException, MairieForbiddenException, InstructeurForbiddenException {
        return this.lireFichierService.execute(id);
    }

}