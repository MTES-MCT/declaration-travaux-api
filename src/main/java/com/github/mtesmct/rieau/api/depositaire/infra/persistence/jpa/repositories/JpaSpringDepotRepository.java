package com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.repositories;

import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaDepot;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSpringDepotRepository extends JpaRepository<JpaDepot, String>{

}