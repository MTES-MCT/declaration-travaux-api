package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Demande;
import com.github.mtesmct.rieau.api.domain.repositories.DemandeRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDemande;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JpaDemandeRepositoryImpl implements DemandeRepository {

    @Autowired
    private JpaDemandeRepository jpaRepository;

    @Override
    public Optional<Demande> findById(String id) {
        Optional<JpaDemande> jpaEntity = this.jpaRepository.findById(id);
        Optional<Demande> identite = Optional.empty();
        if (jpaEntity.isPresent()) {
            identite = Optional.ofNullable(
                    new Demande(jpaEntity.get().getId(), jpaEntity.get().getType(), jpaEntity.get().getEtat(), jpaEntity.get().getDate()));
        }
        return identite;
    }

    @Override
    public List<Demande> findAll() {
        List<Demande> demandes = new ArrayList<Demande>();
        this.jpaRepository.findAll().forEach(jpaEntity -> demandes.add(new Demande(jpaEntity.getId(), jpaEntity.getType(), jpaEntity.getEtat(), jpaEntity.getDate())));
        return demandes;
    }

    @Override
    public Demande save(Demande demande) {
        JpaDemande jpaDemande = JpaDemande.builder().id(demande.getId()).type(demande.getType()).etat(demande.getEtat()).date(new Date(demande.getDate().getTime())).build();
        this.jpaRepository.save(jpaDemande);
        demande.setId(jpaDemande.getId());
        return demande;
    }
    
}