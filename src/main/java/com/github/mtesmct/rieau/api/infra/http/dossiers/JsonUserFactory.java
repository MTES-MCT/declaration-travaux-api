package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import org.springframework.stereotype.Component;

@Component
public class JsonUserFactory {

    public JsonUser toJson(Personne user) {
        JsonUser jsonUser = null;
        if (user != null) {
            jsonUser = new JsonUser(user.identity().toString(), user.email());
        }
        return jsonUser;
    }
}