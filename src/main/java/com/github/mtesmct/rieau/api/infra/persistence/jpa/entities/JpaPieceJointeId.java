package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class JpaPieceJointeId implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String fichierId;
    @Embedded
    @NotNull
    @AttributeOverrides({ @AttributeOverride(name = "type", column = @Column(name = "code_type")),
            @AttributeOverride(name = "numero", column = @Column(name = "code_numero")) })
    private JpaCodePieceJointe code;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "dossier_id", insertable = false, updatable = false)
    private JpaDossier dossier;

    public JpaPieceJointeId() {
    }

    public JpaPieceJointeId(JpaDossier dossier, JpaCodePieceJointe code, String fichierId) {
        this.code = code;
        this.dossier = dossier;
        this.fichierId = fichierId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof JpaPieceJointeId))
            return false;
        JpaPieceJointeId that = (JpaPieceJointeId) o;
        return Objects.equals(this.code, that.code) && Objects.equals(this.dossier, that.dossier)
                && Objects.equals(this.fichierId, that.fichierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dossier, this.code, this.fichierId);
    }

}