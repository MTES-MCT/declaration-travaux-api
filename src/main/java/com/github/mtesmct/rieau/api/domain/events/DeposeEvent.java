package com.github.mtesmct.rieau.api.domain.events;

import com.github.mtesmct.rieau.api.domain.entities.Depot;
import com.github.mtesmct.rieau.api.domain.entities.Utilisateur;

public class DeposeEvent {
    private Depot depot;
    private Utilisateur depositaire;

    public DeposeEvent(Depot depot, Utilisateur depositaire) {
        this.depot = depot;
        this.depositaire = depositaire;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public Utilisateur getDepositaire() {
        return depositaire;
    }

    public void setDepositaire(Utilisateur depositaire) {
        this.depositaire = depositaire;
    }
}