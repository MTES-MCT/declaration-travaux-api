package com.github.mtesmct.rieau.api.depositaire.domain.entities;

import java.io.File;
import java.io.Serializable;

public class PieceJointe implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String code;
    private File file;

    public String getId() {
        return id;
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
}