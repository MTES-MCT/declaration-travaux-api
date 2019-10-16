package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;

import org.springframework.stereotype.Component;

@Component
public class JpaPieceJointeFactory {

    public JpaPieceJointe toJpa(JpaDossier jpaDossier, PieceJointe pieceJointe) {
        if (pieceJointe.code() == null)
            throw new NullPointerException("Le code ne peut pas être nul.");
        JpaCodePieceJointe jpaCodePieceJointe = new JpaCodePieceJointe(pieceJointe.code().type().toString(),
                pieceJointe.code().numero());
        JpaPieceJointe jpaPieceJointe = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, jpaCodePieceJointe, pieceJointe.fichierId().toString()));
        return jpaPieceJointe;
    }

    public PieceJointe fromJpa(Dossier dossier, JpaPieceJointe jpaPieceJointe) {
        if (jpaPieceJointe.getId() == null)
            throw new NullPointerException("L'id de la pièce jointe ne peut pas être nul.");
        if (jpaPieceJointe.getId().getCode() == null)
            throw new NullPointerException("Le code de la pièce jointe ne peut pas être nul.");
        return new PieceJointe(dossier, new CodePieceJointe(EnumTypes.valueOf(jpaPieceJointe.getId().getCode().getType()),
                jpaPieceJointe.getId().getCode().getNumero()), new FichierId(jpaPieceJointe.getId().getFichierId()));
    }
}