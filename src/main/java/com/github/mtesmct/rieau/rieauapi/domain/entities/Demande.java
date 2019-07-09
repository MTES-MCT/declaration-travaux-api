package com.github.mtesmct.rieau.rieauapi.domain.entities;

import java.io.Serializable;
import java.util.Date;

public class Demande implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String id;

    private String type;

    private Date date;

    private String etat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Demande other = (Demande) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Demande [date=" + date + ", etat=" + etat + ", id=" + id + ", type=" + type + "]";
    }

    public Demande(String id, String type, String etat) {
        this.id = id;
        this.type = type;
        this.etat = etat;
    }
    
}