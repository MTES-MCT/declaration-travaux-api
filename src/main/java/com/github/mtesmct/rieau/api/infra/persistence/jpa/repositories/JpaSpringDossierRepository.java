package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

public interface JpaSpringDossierRepository extends JpaRepository<JpaDossier, Long> {
    @Query("select distinct d from Dossier d join fetch d.projet p join fetch d.piecesJointes pj where d.dossierId = ?1")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    Optional<JpaDossier> findOneByDossierIdWithPiecesJointes(String dossierId);
    @Query("select distinct d from Dossier d join fetch d.projet p join fetch d.statuts s where d.dossierId = ?1")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    Optional<JpaDossier> findOneByDossierIdWithStatuts(String dossierId);
    @Query("select distinct d from Dossier d join fetch d.projet p left join fetch d.messages m where d.dossierId = ?1")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    Optional<JpaDossier> findOneByDossierIdWithMessages(String dossierId);

    @Query("select distinct d from Dossier d join fetch d.projet p join fetch d.statuts s where d.deposant.id = ?1")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    List<JpaDossier> findAllByDeposantIdWithStatuts(String deposantId);
    @Query("select distinct d from Dossier d join fetch d.projet p join fetch d.piecesJointes pj where d.deposant.id = ?1")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    List<JpaDossier> findAllByDeposantIdWithPiecesJointes(String deposantId);
    @Query("select distinct d from Dossier d join fetch d.projet p left join fetch d.messages m where d.deposant.id = ?1")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    List<JpaDossier> findAllByDeposantIdWithMessages(String deposantId);

    @Query("select distinct d from Dossier d join fetch d.projet p join fetch d.statuts s where p.adresse.codePostal = ?1")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    List<JpaDossier> findAllByProjetAdresseCodePostalWithStatuts(String codePostal);
    @Query("select distinct d from Dossier d join fetch d.projet p join fetch d.piecesJointes pj where p.adresse.codePostal = ?1")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    List<JpaDossier> findAllByProjetAdresseCodePostalWithPiecesJointes(String codePostal);
    @Query("select distinct d from Dossier d join fetch d.projet p left join fetch d.messages m where p.adresse.codePostal = ?1")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    List<JpaDossier> findAllByProjetAdresseCodePostalWithMessages(String codePostal);

    @Query("select distinct d from Dossier d join fetch d.projet p join fetch d.piecesJointes pj where d.id in (select d1.id from Dossier d1 join d1.piecesJointes pj1 where pj1.id.fichierId = ?1)")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    Optional<JpaDossier> findOneByPiecesJointesIdFichierIdWithPiecesJointes(String fichierId);
    @Query("select distinct d from Dossier d join fetch d.projet p join fetch d.statuts s where d.id in (select d1.id from Dossier d1 join d1.piecesJointes pj1 where pj1.id.fichierId = ?1)")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    Optional<JpaDossier> findOneByPiecesJointesIdFichierIdWithStatuts(String fichierId);
    @Query("select distinct d from Dossier d join fetch d.projet p left join fetch d.messages m where d.id in (select d1.id from Dossier d1 join d1.piecesJointes pj1 where pj1.id.fichierId = ?1)")
    @QueryHints({@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")})
    Optional<JpaDossier> findOneByPiecesJointesIdFichierIdWithMessages(String fichierId);
}