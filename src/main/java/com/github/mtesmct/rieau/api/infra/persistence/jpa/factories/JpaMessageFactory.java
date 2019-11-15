package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Message;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.UserFactory;
import com.github.mtesmct.rieau.api.domain.factories.UserParseException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JpaMessageFactory {
    @Autowired
    private UserFactory userFactory;

    public Message fromJpa(JpaMessage jpaMessage) throws UserParseException {
        if (jpaMessage.getAuteur() == null)
            throw new NullPointerException("L'auteur du message ne peut pas être nul.");
        Optional<User> auteur = this.userFactory.parse(jpaMessage.getAuteur());
        if (auteur.isEmpty())
            throw new NullPointerException("L'auteur du message ne peut pas être nul.");
        Message message = new Message(auteur.get(), jpaMessage.getDate(), jpaMessage.getContenu());
        return message;
    }

    public JpaMessage toJpa(JpaDossier jpaDossier, Message message) {
        if (message.auteur() == null)
            throw new NullPointerException("L'auteur du message ne peut pas être nul.");
        String auteur = message.auteur().toString();
        return new JpaMessage(jpaDossier, auteur, message.date(), message.contenu());
    }
}