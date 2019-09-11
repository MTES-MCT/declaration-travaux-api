package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;

public interface JpaSpringDossierRepository extends NaturalRepository<JpaDossier, String>{
    List<JpaDossier> findAllByDemandeurPersonnePhysiqueId(String demandeurId);
    Optional<JpaDossier> findOptionalByDemandeurPersonnePhysiqueIdAndDossierId(String demandeurId, String id);
}