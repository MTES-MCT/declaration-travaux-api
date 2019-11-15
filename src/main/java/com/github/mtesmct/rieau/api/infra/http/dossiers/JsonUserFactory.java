package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.personnes.User;

import org.springframework.stereotype.Component;

@Component
public class JsonUserFactory {

    public JsonUser toJson(User user) {
        JsonUser jsonUser = null;
        if (user != null) {
            jsonUser = new JsonUser(user.identity().toString(), user.identite().nom(), user.identite().prenom(),user.profils());
        }
        return jsonUser;
    }
}