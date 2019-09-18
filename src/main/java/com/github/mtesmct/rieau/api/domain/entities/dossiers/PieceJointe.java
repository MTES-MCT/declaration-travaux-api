package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class PieceJointe implements ValueObject<PieceJointe> {
    private CodePieceJointe code;
    private Dossier dossier;
    private FichierId fichierId;

    public FichierId fichierId() {
        return this.fichierId;
    }

    public Dossier dossier() {
        return this.dossier;
    }

    public CodePieceJointe code() {
        return this.code;
    }

    @Override
    public String toString() {
        return "PieceJointe={" + this.code.toString() + ", fichierId={" + this.fichierId.toString() + "}, "
                + this.dossier.toString() + "}";
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final PieceJointe other = (PieceJointe) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.code, this.fichierId, this.dossier.identity());
    }

    @Override
    public boolean hasSameValuesAs(PieceJointe other) {
        return other != null && this.code.equals(other.code) && this.dossier.equals(other.dossier) && this.fichierId.equals(other.fichierId);
    }

    public PieceJointe(final Dossier dossier, final CodePieceJointe code, final FichierId fichierId) {
        if (dossier == null)
            throw new NullPointerException("Le dossier de la pièce jointe ne peut pas être nul");
        this.dossier = dossier;
        if (code == null)
            throw new NullPointerException("Le code de la pièce jointe ne peut pas être nul");
        this.code = code;
        if (fichierId == null)
            throw new NullPointerException("Le fichier id de la pièce jointe ne peut pas être nul");
        this.fichierId = fichierId;
    }

    public boolean isCerfa() {
        return this.code.isCerfa();
    }

    public boolean isAJoindre() {
        return this.dossier.piecesAJoindre().contains(this);
    }
}