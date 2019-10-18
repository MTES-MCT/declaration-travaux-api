package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonUser {
    private String id;
    private String email;

    public JsonUser(String id, String email) {
        this.id = id;
        this.email = email;
    }
}