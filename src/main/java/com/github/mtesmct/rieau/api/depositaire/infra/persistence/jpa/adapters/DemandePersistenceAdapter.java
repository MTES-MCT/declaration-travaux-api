package com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.adapters;

import java.sql.Date;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Demande;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaDemande;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DemandePersistenceAdapter {
    public JpaDemande toJpa(Demande demande){
        return JpaDemande.builder().id(demande.getId()).type(demande.getType()).etat(demande.getEtat()).date(new Date(demande.getDate().getTime())).build();
    }
    public Demande fromJpa(JpaDemande jpaDemande){
        return new Demande(jpaDemande.getId(), jpaDemande.getType(), jpaDemande.getEtat(), new java.util.Date(jpaDemande.getDate().getTime()));
    }
}