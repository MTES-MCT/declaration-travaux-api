package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypePieceJointe;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "PieceJointe")
@Table(name = "pieces_jointes")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
public class JpaPieceJointe {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = false)
    private String fileName;
    @Column(nullable = false, unique = false)
    private String fileType;
    @Column(nullable = false, unique = true)
    private String storageId;
    @Column(nullable = false, unique = false)
    private String code;
    @Version
    private Long version;
    @Enumerated
    private TypePieceJointe type;
    @Column(nullable = false)
    private Integer numero;
    @ManyToOne
    private JpaDossier dossier;

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
        return Objects.hash(id);
    }
}