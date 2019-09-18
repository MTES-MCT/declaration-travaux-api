package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class PiecesAJoindre implements ValueObject<PiecesAJoindre> {
    private Dossier dossier;

    @Override
    public String toString() {
        return this.dossier.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final PiecesAJoindre other = (PiecesAJoindre) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dossier);
    }

    @Override
    public boolean hasSameValuesAs(PiecesAJoindre other) {
        return other != null && this.dossier.equals(other.dossier);
    }

    public boolean contains(PieceJointe pieceJointe){
        return pieceJointe != null && this.codesPiecesAJoindre() != null && this.codesPiecesAJoindre().contains(pieceJointe.code());
    }

    public PiecesAJoindre(Dossier dossier) {
        if (dossier == null)
            throw new NullPointerException("Le dossier ne peut pas être nul");
        this.dossier = dossier;
    }

    public List<CodePieceJointe> codesPiecesAJoindre(){
        List<CodePieceJointe> codes = new ArrayList<CodePieceJointe>();        
        if (this.dossier != null && this.dossier.type() !=  null)
            codes = this.dossier.type().codesPiecesAJoindre();
        return codes;
    }
}