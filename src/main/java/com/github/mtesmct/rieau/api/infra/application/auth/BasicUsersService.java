package com.github.mtesmct.rieau.api.infra.application.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.github.mtesmct.rieau.api.application.auth.MockUsers;
import com.github.mtesmct.rieau.api.application.auth.Roles;
import com.github.mtesmct.rieau.api.application.auth.UsersService;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.UserFactory;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasicUsersService implements UsersService {

        public static final String EMAIL_DOMAIN = "@monfai.fr";

        private Map<String, User> users;
        @Autowired
        private UserFactory userFactory;

        @PostConstruct
        public void initUsers() throws CommuneNotFoundException {
                this.users = new HashMap<String, User>();
                this.users.put(MockUsers.JEAN_MARTIN,
                                this.userFactory.creer(MockUsers.JEAN_MARTIN, MockUsers.JEAN_MARTIN + EMAIL_DOMAIN,
                                                Sexe.HOMME, noms(MockUsers.JEAN_MARTIN)[1],
                                                noms(MockUsers.JEAN_MARTIN)[0], "44100", "1", "rue des Fleurs", "", "",
                                                "", new String[] { Roles.BETA, Roles.DEPOSANT }));
                this.users.put(MockUsers.JACQUES_DUPONT, this.userFactory.creer(MockUsers.JACQUES_DUPONT,
                                MockUsers.JACQUES_DUPONT + EMAIL_DOMAIN, Sexe.HOMME, noms(MockUsers.JACQUES_DUPONT)[1],
                                noms(MockUsers.JACQUES_DUPONT)[0], "44100", "1", "rue des Fleurs", "", "", "",
                                new String[] { Roles.INSTRUCTEUR }));
                this.users.put(MockUsers.CLAIRE_DENIS, this.userFactory.creer(MockUsers.CLAIRE_DENIS,
                                MockUsers.CLAIRE_DENIS + EMAIL_DOMAIN, Sexe.FEMME, noms(MockUsers.CLAIRE_DENIS)[1],
                                noms(MockUsers.CLAIRE_DENIS)[0], "44100", "1", "rue des Fleurs", "", "", "",
                                new String[] { Roles.BETA, Roles.DEPOSANT }));
                this.users.put(MockUsers.MADAME_LE_MAIRE, this.userFactory.creer(MockUsers.MADAME_LE_MAIRE,
                                MockUsers.MADAME_LE_MAIRE + EMAIL_DOMAIN, Sexe.FEMME,
                                noms(MockUsers.MADAME_LE_MAIRE)[1], noms(MockUsers.MADAME_LE_MAIRE)[0], "44100", "1",
                                "rue des Fleurs", "", "", "", new String[] { Roles.MAIRIE }));
        }

        public String[] noms(String id) {
                return id.split("\\.");
        }

        @Override
        public Optional<User> findUserById(String id) {
                Optional<User> user = Optional.empty();
                user = Optional.ofNullable(this.users.get(id));
                return user;
        }

}