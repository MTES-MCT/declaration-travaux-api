package com.github.mtesmct.rieau.api.depositaire.infra.adau;

import java.util.UUID;

import com.github.mtesmct.rieau.api.depositaire.domain.services.NoNationalService;

import org.springframework.stereotype.Service;

@Service
public class MockUUIDNoNationalService implements NoNationalService {

    @Override
    public String getNew() {
        return UUID.randomUUID().toString();
    }
}