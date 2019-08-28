package com.github.mtesmct.rieau.api.domain.entities;

import java.io.File;
import java.io.Serializable;

public class PieceJointe implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String code;
    private File file;
    private Depot depot;

    public String getId() {
        return id;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public void setId(String id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PieceJointe(String id, String code, File file, Depot depot) {
        this.id = id;
        this.code = code;
        this.file = file;
        this.depot = depot;
    }
}