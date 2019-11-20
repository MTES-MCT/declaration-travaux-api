package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "Message")
@Table(name = "messages")
@Getter
@Setter
public class JpaMessage {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = true, unique = false)
    @NotNull
    private LocalDateTime date;
    @NotNull
    @Column(nullable = false, columnDefinition="TEXT")
    private String auteur;
    @Column(nullable = true, columnDefinition="TEXT")
    private String contenu;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id")
    private JpaDossier dossier;

    public JpaMessage(@NotNull JpaDossier dossier, @NotNull String auteur, @NotNull LocalDateTime date, String contenu) {
        this();
        this.dossier = dossier;
        this.auteur = auteur;
        this.date = date;
        this.contenu = contenu;
    }

    public JpaMessage() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JpaMessage other = (JpaMessage) o;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}