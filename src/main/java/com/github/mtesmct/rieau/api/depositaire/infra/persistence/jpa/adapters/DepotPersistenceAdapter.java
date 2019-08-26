package com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.adapters;

import java.sql.Date;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaDepot;

import org.springframework.stereotype.Component;

@Component
public class DepotPersistenceAdapter {
    public JpaDepot toJpa(Depot depot){
        return JpaDepot.builder().noNational(depot.getId()).type(depot.getType()).etat(depot.getEtat()).date(new Date(depot.getDate().getTime())).build();
    }
    public Depot fromJpa(JpaDepot jpaDepot){
        return new Depot(jpaDepot.getNoNational(), jpaDepot.getType(), jpaDepot.getEtat(), new java.util.Date(jpaDepot.getDate().getTime()));
    }
}