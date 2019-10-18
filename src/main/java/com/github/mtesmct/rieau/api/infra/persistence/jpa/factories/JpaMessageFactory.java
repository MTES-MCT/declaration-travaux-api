package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Message;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaMessage;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaUser;

import org.springframework.stereotype.Component;

@Component
public class JpaMessageFactory {

    public Message fromJpa(JpaMessage jpaMessage) {
        if (jpaMessage.getAuteur() == null)
            throw new NullPointerException("L'auteur du message ne peut pas être nul.");
        Personne auteur = new Personne(jpaMessage.getAuteur().getId(), jpaMessage.getAuteur().getEmail());
        Message message = new Message(auteur, jpaMessage.getDate(), jpaMessage.getContenu());
        return message;
    }

    public JpaMessage toJpa(JpaDossier jpaDossier, Message message) {
        if (message.auteur() == null)
            throw new NullPointerException("L'auteur du message ne peut pas être nul.");
        JpaUser auteur = new JpaUser(message.auteur().identity().toString(), message.auteur().email());
        return new JpaMessage(jpaDossier, auteur, message.date(), message.contenu());
    }
}