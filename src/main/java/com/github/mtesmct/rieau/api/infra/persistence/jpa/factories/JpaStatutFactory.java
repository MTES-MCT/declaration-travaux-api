package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Statut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.repositories.TypeStatutDossierRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaStatut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JpaStatutFactory {

    @Autowired
    private TypeStatutDossierRepository statutDossierRepository;

    public Statut fromJpa(JpaStatut jpaStatut) throws TypeStatutNotFoundException {
        Optional<TypeStatut> type = this.statutDossierRepository.findByStatut(jpaStatut.getStatut());
        if (type.isEmpty())
            throw new TypeStatutNotFoundException(jpaStatut.getStatut());            
        Statut statutDossier = new Statut(type.get(), jpaStatut.getDateDebut());
        return statutDossier;
    }

    public JpaStatut toJpa(JpaDossier jpaDossier, Statut statut) {
        return new JpaStatut(jpaDossier, statut.type().statut(), statut.dateDebut());
    }
}