package com.github.mtesmct.rieau.api.infra.persistence.jpa.adapters;

import com.github.mtesmct.rieau.api.domain.entities.Utilisateur;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaIdentite;

import org.springframework.stereotype.Component;

@Component
public class JpaIdentiteAdapter {
    public JpaIdentite toJpa(Utilisateur identite){
        return JpaIdentite.builder().id(identite.getId()).nom(identite.getNom()).prenom(identite.getPrenom()).email(identite.getEmail()).build();
    }
    public Utilisateur fromJpa(JpaIdentite jpaIdentite){
        return new Utilisateur(jpaIdentite.getId(), jpaIdentite.getNom(), jpaIdentite.getPrenom(), jpaIdentite.getEmail());
    }
}