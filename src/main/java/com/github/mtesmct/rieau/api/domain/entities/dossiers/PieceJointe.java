package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.io.File;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class PieceJointe implements ValueObject<PieceJointe> {
    private CodePieceJointe code;
    private Long version;
    private Dossier dossier;
    private File file;
    private String mimeType;

    public boolean isObligatoire(){
        return this.code.isCerfa();
    }
    public File file(){
        return this.file;
    }
    public String mimeType(){
        return this.mimeType;
    }
    public Dossier dossier(){
        return this.dossier;
    }
    public CodePieceJointe code(){
        return this.code;
    }

    @Override
    public String toString() {
        return this.code.toString() + "-" + this.file.getName() + "-" + this.version.toString() + "-" + this.dossier.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final PieceJointe other = (PieceJointe) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.code,this.file.getName(),this.version, this.dossier.identity());
    }

    @Override
    public boolean hasSameValuesAs(PieceJointe other) {
        return other != null && this.code.hasSameValuesAs(other.code) && this.version.equals(other.version) && this.dossier.hasSameIdentityAs(other.dossier) && this.file.equals(other.file);
    }

    public PieceJointe(final CodePieceJointe code, final File file) {
        if (code == null)
            throw new NullPointerException("Le code de la pièce jointe ne peut être nul");
        this.code = code;
        this.file = file;
    }

    public boolean isCerfa(){
        return this.code.isCerfa();
    }
}