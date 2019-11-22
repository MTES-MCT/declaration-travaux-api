package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

public class ParcelleCadastrale implements ValueObject<ParcelleCadastrale> {

    private String prefixe;
    private String section;
    private String numero;

    public String prefixe() {
        return this.prefixe;
    }

    public String section() {
        return this.section;
    }

    public String numero() {
        return this.numero;
    }

    private static String joining() {
        return "-";
    }

    @Override
    public boolean hasSameValuesAs(ParcelleCadastrale other) {
        return other != null && Objects.equals(this.prefixe, other.prefixe)
                && Objects.equals(this.section, other.section) && Objects.equals(this.numero, other.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.numero, this.prefixe, this.section);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final ParcelleCadastrale other = (ParcelleCadastrale) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public String toString() {
        return "ParcelleCadastrale={ prefixe={" + this.prefixe + "}, section={" + this.section + "}, numéro={"
                + this.numero + "} }";
    }

    public String toFlatString() {
        return this.prefixe + joining() + this.section + joining() + this.numero;
    }

    public static Optional<ParcelleCadastrale> parse(String text) throws PatternSyntaxException {
        Optional<ParcelleCadastrale> parcelle = Optional.empty();
        String[] splitted = text.split(joining());
        if (splitted.length == 3) {
            parcelle = Optional.ofNullable(new ParcelleCadastrale(splitted[0], splitted[1], splitted[2]));
        }
        return parcelle;
    }

    public ParcelleCadastrale(String prefixe, String section, String numero) {
        if (Objects.isNull(prefixe) || prefixe.isBlank())
            throw new NullPointerException(
                    "Le préfixe de la référence cadastrale de la parcelle ne peut pas être nulle");
        this.prefixe = prefixe;
        if (Objects.isNull(section) || section.isBlank())
            throw new NullPointerException(
                    "La section de la référence cadastrale de la parcelle ne peut pas être nulle");
        this.section = section;
        if (Objects.isNull(numero) || numero.isBlank())
            throw new NullPointerException(
                    "Le numéro de la référence cadastrale de la parcelle ne peut pas être nulle");
        this.numero = numero;
    }

}