package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProjetRepository extends JpaRepository<JpaProjet, Long> {
}