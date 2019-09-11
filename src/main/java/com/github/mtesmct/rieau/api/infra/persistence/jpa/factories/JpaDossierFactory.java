package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JpaDossierFactory {
    @Autowired
    private JpaPersonnePhysiqueFactory jpaPersonnePhysiqueFactory;
    public JpaDossier toJpa(Dossier dossier){
        JpaDossier jpaDossier = JpaDossier.builder().dossierId(dossier.identity().toString()).statut(dossier.statut()).demandeur(jpaPersonnePhysiqueFactory.toJpa(dossier.demandeur())).date(dossier.dateDepot()).build();
        return jpaDossier;
    }
    public Dossier fromJpa(JpaDossier jpaDossier){
        return new Dossier(new DossierId(jpaDossier.getDossierId()), jpaPersonnePhysiqueFactory.fromJpa(jpaDossier.getDemandeur()), jpaDossier.getDate());
    }
}