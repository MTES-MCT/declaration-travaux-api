package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.List;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;

import org.springframework.data.jpa.repository.Query;

public interface JpaSpringDossierRepository extends NaturalRepository<JpaDossier, String>{
    List<JpaDossier> findAllByDeposantId(String deposantId);
    boolean existsByDeposantIdAndPiecesJointesIdFichierId(String deposantId, String fichierId);
    @Query("select d from Dossier d join Projet p where p.id = d.id and p.adresse.codePostal = ?1")
	List<JpaDossier> findAllByProjetAdresseCodePostal(String codePostal);
}