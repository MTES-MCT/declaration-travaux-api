package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaIdentite;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSpringIdentiteRepository extends JpaRepository<JpaIdentite, String>{

}