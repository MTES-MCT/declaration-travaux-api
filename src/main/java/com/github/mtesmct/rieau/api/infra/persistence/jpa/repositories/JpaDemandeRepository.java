package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDemande;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDemandeRepository extends JpaRepository<JpaDemande, String>{

}