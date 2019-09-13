package com.github.mtesmct.rieau.api.infra.application.dossiers;

import java.util.List;

import com.github.mtesmct.rieau.api.application.dossiers.ApplicationListerMesDossiersService;
import com.github.mtesmct.rieau.api.application.dossiers.ListerMesDossiersService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SpringListerMesDossiersService implements ListerMesDossiersService {

    @Autowired
    private ApplicationListerMesDossiersService applicationListerMesDossiersService;

    @Override
    public List<Dossier> execute() {
        return this.applicationListerMesDossiersService.execute();
    }
    
}