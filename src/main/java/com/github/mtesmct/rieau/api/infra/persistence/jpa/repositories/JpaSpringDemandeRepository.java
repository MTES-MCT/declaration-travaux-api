package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDemande;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSpringDemandeRepository extends JpaRepository<JpaDemande, String>{

}