package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import java.io.File;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;

import org.springframework.stereotype.Component;

@Component
public class JpaPieceJointeFactory {
    public JpaPieceJointe toJpa(PieceJointe pieceJointe){
        return JpaPieceJointe.builder().fileName(pieceJointe.file().getName()).fileType(pieceJointe.mimeType()).storageId("todo").type(pieceJointe.code().type()).build();
    }
    public PieceJointe fromJpa(JpaPieceJointe jpaPieceJointe){
        return new PieceJointe(new CodePieceJointe(jpaPieceJointe.getType(), jpaPieceJointe.getNumero()), new File("todo"));
    }
}