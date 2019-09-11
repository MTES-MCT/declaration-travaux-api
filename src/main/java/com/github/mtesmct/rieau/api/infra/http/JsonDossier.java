package com.github.mtesmct.rieau.api.infra.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JsonDossier {
    private String id;
    private String type;
    private String statut;
    private String date;

}