package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Comparator;

public class TypeStatutComparator implements Comparator<TypeStatut> {

    @Override
    public int compare(TypeStatut type1, TypeStatut type2) {
        int compared = 0;
        compared = type1.ordre() - type2.ordre();
        if (compared == 0)
            compared = type1.identity().equals(type2.identity()) ? 0 : 1;
        return compared;
    }

}