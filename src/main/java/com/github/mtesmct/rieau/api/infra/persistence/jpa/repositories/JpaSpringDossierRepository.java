package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.List;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;

public interface JpaSpringDossierRepository extends NaturalRepository<JpaDossier, String>{
    List<JpaDossier> findAllByDeposantId(String deposantId);
}