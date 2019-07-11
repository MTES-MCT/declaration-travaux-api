package com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.repositories;

import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaIdentite;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSpringIdentiteRepository extends JpaRepository<JpaIdentite, String>{

}