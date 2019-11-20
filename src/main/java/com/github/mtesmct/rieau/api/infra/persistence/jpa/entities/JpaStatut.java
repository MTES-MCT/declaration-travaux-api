package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "Statut")
@Table(name = "statuts")
@Getter
@Setter
public class JpaStatut {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, length = 14)
    @NotNull
    @Enumerated(EnumType.STRING)
    private EnumStatuts statut;
    @Column(nullable = true, unique = false)
    private LocalDateTime dateDebut;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "dossier_id")
    private JpaDossier dossier;

    public JpaStatut(@NotNull JpaDossier dossier, @NotNull EnumStatuts statut, LocalDateTime dateDebut) {
        this(statut);
        this.dateDebut = dateDebut;
    }

    public JpaStatut(@NotNull EnumStatuts statut) {
        this.statut = statut;
    }

    public JpaStatut() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaStatut jpaStatut = (JpaStatut) o;
        return Objects.equals(this.id, jpaStatut.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}