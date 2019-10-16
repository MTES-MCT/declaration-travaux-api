package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.List;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class TypeDossier implements ValueObject<TypeDossier> {
    private String code;
    private List<String> piecesAJoindreObligatoires;
    private EnumTypes type;

    public TypeDossier(EnumTypes type, String code, List<String> piecesAJoindreObligatoires) {
        this.type = type;
        this.code = code;
        this.piecesAJoindreObligatoires = piecesAJoindreObligatoires;
    }

    public EnumTypes type() {
        return this.type;
    }

    public List<String> piecesAJoindreObligatoires() {
        return this.piecesAJoindreObligatoires;
    }

    public String code() {
        return this.code;
    }

    @Override
    public boolean hasSameValuesAs(TypeDossier other) {
        return other != null && Objects.equals(this.type, other.type) && Objects.equals(this.code, other.code)
                && Objects.equals(this.piecesAJoindreObligatoires, other.piecesAJoindreObligatoires);
    }

    @Override
    public String toString() {
        return "TypeDossier={ type={" + Objects.toString(this.type) + "}, piecesAJoindreObligatoires={"
                + Objects.toString(this.piecesAJoindreObligatoires) + "}, code={" + this.code + "} }";
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final TypeDossier other = (TypeDossier) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.code, this.piecesAJoindreObligatoires);
    }
}