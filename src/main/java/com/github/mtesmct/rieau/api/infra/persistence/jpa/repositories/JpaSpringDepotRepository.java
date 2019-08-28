package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDepot;

public interface JpaSpringDepotRepository extends NaturalRepository<JpaDepot, String>{
    List<JpaDepot> findByDepositaire(String depositaire);
    Optional<JpaDepot> findOneByDepositaireAndNoNational(String depositaire, String id);
}