package com.github.mtesmct.rieau.api.infra.nonational;

import java.util.UUID;

import com.github.mtesmct.rieau.api.application.NoNationalService;

import org.springframework.stereotype.Service;

@Service
public class MockUUIDNoNationalService implements NoNationalService {

    @Override
    public String getNew() {
        return UUID.randomUUID().toString();
    }
}