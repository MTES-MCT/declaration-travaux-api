package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaSpringDossierRepository extends JpaRepository<JpaDossier, Long> {
    @Query("select distinct d from Projet p join p.dossier d join fetch d.piecesJointes pj join fetch d.statuts s left join fetch d.messages m where d.dossierId = ?1")
    List<JpaDossier> findAllByDossierId(String dossierId);

    @Query("select distinct d from Projet p join p.dossier d join fetch d.piecesJointes pj join fetch d.statuts s left join fetch d.messages m where d.deposant.id = ?1")
    List<JpaDossier> findAllByDeposantId(String deposantId);

    @Query("select distinct d from Projet p join p.dossier d join fetch d.piecesJointes pj join fetch d.statuts s left join fetch d.messages m where p.adresse.codePostal = ?1")
    List<JpaDossier> findAllByProjetAdresseCodePostal(String codePostal);

    @Query("select distinct d from Projet p join p.dossier d join fetch d.piecesJointes pj join fetch d.statuts s left join fetch d.messages m where pj.id.fichierId = ?1")
    Optional<JpaDossier> findOneByPiecesJointesIdFichierId(String fichierId);
}