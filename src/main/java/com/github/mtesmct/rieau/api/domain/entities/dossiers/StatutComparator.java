package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Comparator;

public class StatutComparator implements Comparator<Statut> {

    @Override
    public int compare(Statut statut1, Statut statut2) {
        int compared = 0;
        compared = statut1.type().ordre() - statut2.type().ordre();
        if (compared == 0)
            compared = statut1.dateDebut().compareTo(statut2.dateDebut());
        return compared;
    }

}