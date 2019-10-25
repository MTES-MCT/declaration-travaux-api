package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.application.auth.MockUsers;
import com.github.mtesmct.rieau.api.application.auth.UsersService;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.factories.PersonneFactory;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BasicUsersService implements UsersService {

    public static final String EMAIL_DOMAIN = "@monfai.fr";

    private Map<String, Personne> users;
    @Autowired
    private PersonneFactory personneFactory;

    @PostConstruct
    public void initUsers() throws CommuneNotFoundException {
        this.users = new HashMap<String, Personne>();
        this.users.put(MockUsers.JEAN_MARTIN,
                this.personneFactory.creer(MockUsers.JEAN_MARTIN, MockUsers.JEAN_MARTIN + EMAIL_DOMAIN, Sexe.HOMME,
                        noms(MockUsers.JEAN_MARTIN)[1], noms(MockUsers.JEAN_MARTIN)[0], "01/01/1970", "44100", "44100",
                        "1", "rue des Fleurs", "", "", ""));
        this.users.put(MockUsers.JACQUES_DUPONT,
                this.personneFactory.creer(MockUsers.JACQUES_DUPONT, MockUsers.JACQUES_DUPONT + EMAIL_DOMAIN,
                        Sexe.HOMME, noms(MockUsers.JACQUES_DUPONT)[1], noms(MockUsers.JACQUES_DUPONT)[0], "01/01/1970",
                        "44100", "44100", "1", "rue des Fleurs", "", "", ""));
        this.users.put(MockUsers.CLAIRE_DENIS,
                this.personneFactory.creer(MockUsers.CLAIRE_DENIS, MockUsers.CLAIRE_DENIS + EMAIL_DOMAIN, Sexe.FEMME,
                        noms(MockUsers.CLAIRE_DENIS)[1], noms(MockUsers.CLAIRE_DENIS)[0], "01/01/1970", "44100",
                        "44100", "1", "rue des Fleurs", "", "", ""));
        this.users.put(MockUsers.MADAME_LE_MAIRE,
                this.personneFactory.creer(MockUsers.MADAME_LE_MAIRE, MockUsers.MADAME_LE_MAIRE + EMAIL_DOMAIN,
                        Sexe.FEMME, noms(MockUsers.MADAME_LE_MAIRE)[1], noms(MockUsers.MADAME_LE_MAIRE)[0],
                        "01/01/1970", "44100", "44100", "1", "rue des Fleurs", "", "", ""));
    }

    public String[] noms(String id) {
        return id.split("\\.");
    }

    @Override
    public Optional<Personne> findUserById(String id) {
        Optional<Personne> user = Optional.empty();
        user = Optional.ofNullable(this.users.get(id));
        return user;
    }

}