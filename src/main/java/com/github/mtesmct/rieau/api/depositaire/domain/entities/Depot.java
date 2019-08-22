package com.github.mtesmct.rieau.api.depositaire.domain.entities;

import java.io.Serializable;
import java.util.Date;

public class Depot implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Etat { instruction, incomplet, consultations, complet, clos } 
    public enum Type { dp, pcmi } 
    
    private String id;

    private Type type;

    private Date date;

    private Etat etat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
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
        Depot other = (Depot) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Dépôt [date=" + date + ", etat=" + etat.toString() + ", id=" + id + ", type=" + type.toString() + "]";
    }

    public Depot(String id, Type type, Date date) {
        this.id = id;
        this.type = type;
        this.etat = Etat.instruction;
        this.date = date;
    }

    public Depot(String id, Type type, Etat etat, Date date) {
        this.id = id;
        this.type = type;
        this.etat = etat;
        this.date = date;
    }
    
}