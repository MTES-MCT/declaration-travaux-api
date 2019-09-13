package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;

import org.springframework.stereotype.Component;

@Component
public class JpaDossierFactory {
    public JpaDossier toJpa(Dossier dossier){
        JpaDossier jpaDossier = JpaDossier.builder().dossierId(dossier.identity().toString()).statut(dossier.statut()).deposantId(dossier.deposant().identity().toString()).deposantEmail(dossier.deposant().email()).date(dossier.dateDepot()).type(dossier.type).build();
        return jpaDossier;
    }
    public Dossier fromJpa(JpaDossier jpaDossier){
        // TODO cerfa piece jointe
        return new Dossier(new DossierId(jpaDossier.getDossierId()), new PersonnePhysique(jpaDossier.getDeposantId(), jpaDossier.getDeposantEmail()), jpaDossier.getDate(), null, jpaDossier.getType());
    }
}