package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Comparator;

public class StatutComparator implements Comparator<Statut> {

    @Override
    public int compare(Statut statut1, Statut statut2) {
        int compared = 0;
        compared = statut1.type().ordre() - statut2.type().ordre();
        if (compared == 0) {
            if ((statut1.type().unique() && statut2.type().unique())
                    || (!statut1.type().unique() && !statut2.type().unique()))
                compared = statut1.dateDebut().compareTo(statut2.dateDebut());
            if ((statut1.type().unique() && !statut2.type().unique()))
                compared = -1;
            if ((!statut1.type().unique() && statut2.type().unique()))
                compared = 1;
        }
        return compared;
    }

}