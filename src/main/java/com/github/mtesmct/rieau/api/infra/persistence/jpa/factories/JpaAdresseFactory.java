package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Adresse;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.CommuneService;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaAdresse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaAdresseFactory {

    @Autowired
    private CommuneService communeService;

    public Adresse fromJpa(JpaAdresse jpaAdresse) throws CommuneNotFoundException {
        Optional<Commune> commune = this.communeService.findByCodeCodePostal(jpaAdresse.getCodePostal());
        if (commune.isEmpty())
            throw new CommuneNotFoundException(jpaAdresse.getCodePostal());
        return new Adresse(jpaAdresse.getNumero(), jpaAdresse.getVoie(), jpaAdresse.getLieuDit(), commune.get(),
                jpaAdresse.getBp(), jpaAdresse.getCedex());
    }

    public JpaAdresse toJpa(Adresse adresse) {
        return new JpaAdresse(adresse.numero(), adresse.voie(), adresse.lieuDit(), adresse.commune().codePostal(),
                adresse.bp(), adresse.cedex());
    }
}