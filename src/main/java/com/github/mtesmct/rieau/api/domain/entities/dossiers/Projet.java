package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class Projet implements ValueObject<Projet>{



    @Override
    public boolean hasSameValuesAs(Projet other) {
        return other!=null;
    }

}