package com.github.mtesmct.rieau.api.infra.persistence.jpa.adapters;

import com.github.mtesmct.rieau.api.domain.entities.Identite;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaIdentite;

import org.springframework.stereotype.Component;

@Component
public class IdentitePersistenceAdapter {
    public JpaIdentite toJpa(Identite identite){
        return JpaIdentite.builder().id(identite.getId()).nom(identite.getNom()).prenom(identite.getPrenom()).email(identite.getEmail()).build();
    }
    public Identite fromJpa(JpaIdentite jpaIdentite){
        return new Identite(jpaIdentite.getId(), jpaIdentite.getNom(), jpaIdentite.getPrenom(), jpaIdentite.getEmail());
    }
}