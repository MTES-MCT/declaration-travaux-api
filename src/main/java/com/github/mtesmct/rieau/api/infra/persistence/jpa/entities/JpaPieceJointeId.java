package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

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