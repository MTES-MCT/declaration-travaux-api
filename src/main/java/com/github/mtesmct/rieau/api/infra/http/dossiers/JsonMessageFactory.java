package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Message;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JsonMessageFactory {
    @Autowired
    private DateConverter<LocalDateTime> localDateTimeConverter;
    @Autowired
    private JsonUserFactory jsonUserFactory;

    public JsonMessage toJson(Message message) {
        JsonMessage jsonMessage = null;
        if (message != null) {
            jsonMessage = new JsonMessage(this.jsonUserFactory.toJson(message.auteur()), message.contenu(), this.localDateTimeConverter.formatDateTime(message.date()));
        }
        return jsonMessage;
    }
}