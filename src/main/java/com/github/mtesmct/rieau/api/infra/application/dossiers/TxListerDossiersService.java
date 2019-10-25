package com.github.mtesmct.rieau.api.infra.application.dossiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.AppListerDossiersService;
import com.github.mtesmct.rieau.api.application.dossiers.ListerDossiersService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TxListerDossiersService implements ListerDossiersService {

    @Autowired
    private AppListerDossiersService applicationListerDossiersService;

    @Override
    public List<Dossier> execute() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        return this.applicationListerDossiersService.execute();
    }
    
}