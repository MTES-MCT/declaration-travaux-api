package com.github.mtesmct.rieau.api.infra.persistence.jpa.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "Message")
@Table(name = "messages")
@Getter
@Setter
public class JpaMessage {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = true, unique = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date date;
    @Embedded
    @NotNull
    @AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "auteur_id")),
            @AttributeOverride(name = "email", column = @Column(name = "auteur_email")) })
    private JpaUser auteur;
    private String contenu;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "dossier_id")
    private JpaDossier dossier;

    public JpaMessage(@NotNull JpaDossier dossier, @NotNull JpaUser auteur, @NotNull Date date, String contenu) {
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