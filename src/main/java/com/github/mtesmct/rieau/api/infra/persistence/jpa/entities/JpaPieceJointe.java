package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "PieceJointe")
@Table(name = "pieces_jointes")
@Getter
@Setter
public class JpaPieceJointe {
    @EmbeddedId
    private JpaPieceJointeId id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaPieceJointe jpaPieceJointe = (JpaPieceJointe) o;
        return Objects.equals(id, jpaPieceJointe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public JpaPieceJointe() {
        this.createdOn = new Date();
    }

    public JpaPieceJointe(@NotNull JpaPieceJointeId id) {
        this();
        this.id = id;
    }

    @Transient
    public boolean isCerfa() {
        return this.id!= null && this.id.getCode() != null && this.id.getCode().isCerfa();
    }
}