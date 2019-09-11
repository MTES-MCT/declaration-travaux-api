package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;

public interface PersonnePhysiqueRepository {
    public Optional<PersonnePhysique> findByEmail(String email);
}