package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonMessageFactory {
    @Autowired
    private JsonUserFactory jsonUserFactory;

    public JsonMessage toJson(Message message) {
        JsonMessage jsonMessage = null;
        if (message != null) {
            jsonMessage = new JsonMessage(this.jsonUserFactory.toJson(message.auteur()), message.contenu(), message.date());
        }
        return jsonMessage;
    }
}