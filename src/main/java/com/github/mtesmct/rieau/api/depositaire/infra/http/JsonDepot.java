package com.github.mtesmct.rieau.api.depositaire.infra.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JsonDepot {
    private String id;
    private String type;
    private String etat;
    private String date;

}