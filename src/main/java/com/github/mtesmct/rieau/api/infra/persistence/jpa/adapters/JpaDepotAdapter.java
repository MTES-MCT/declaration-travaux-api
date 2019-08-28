package com.github.mtesmct.rieau.api.infra.persistence.jpa.adapters;

import com.github.mtesmct.rieau.api.domain.entities.Depot;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDepot;

import org.springframework.stereotype.Component;

@Component
public class JpaDepotAdapter {
    public JpaDepot toJpa(Depot depot){
        return JpaDepot.builder().noNational(depot.getId()).type(depot.getType()).etat(depot.getEtat()).date(depot.getDate()).build();
    }
    public Depot fromJpa(JpaDepot jpaDepot){
        return new Depot(jpaDepot.getNoNational(), jpaDepot.getType(), jpaDepot.getEtat(), jpaDepot.getDate(), jpaDepot.getDepositaire());
    }
}