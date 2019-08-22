package com.github.mtesmct.rieau.api.depositaire.infra.iddepot;

import java.util.UUID;

import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdDepotRepository;

import org.springframework.stereotype.Repository;

@Repository
public class UUIDDepotRepository implements IdDepotRepository {

    @Override
    public String getNew() {
        return UUID.randomUUID().toString();
    }
}