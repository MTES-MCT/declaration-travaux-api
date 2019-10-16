package com.github.mtesmct.rieau.api.infra.file.pdf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CerfaFormMapper {

    private Map<String, Map<String, String>> champsForm;

    public CerfaFormMapper() {
        this.champsForm = new HashMap<String, Map<String, String>>();
        Map<String, String> champsFormPCMI = new HashMap<String, String>();
        champsFormPCMI = new HashMap<String, String>();
        champsFormPCMI.put("numeroVoie", "topmostSubform[0].Page3[0].T2Q_numero[0]");
        champsFormPCMI.put("voie", "topmostSubform[0].Page3[0].T2V_voie[0]");
        champsFormPCMI.put("lieuDit", "topmostSubform[0].Page3[0].T2W_lieudit[0]");
        champsFormPCMI.put("codePostal", "topmostSubform[0].Page3[0].T2C_code[0]");
        champsFormPCMI.put("bp", "topmostSubform[0].Page3[0].T2B_boite[0]");
        champsFormPCMI.put("cedex", "topmostSubform[0].Page3[0].T2X_cedex[0]");
        champsFormPCMI.put("prefixe", "topmostSubform[0].Page3[0].T2F_prefixe[0]");
        champsFormPCMI.put("section", "topmostSubform[0].Page3[0].T2S_section[0]");
        champsFormPCMI.put("numeroCadastre", "topmostSubform[0].Page3[0].T2N_numero[0]");
        champsFormPCMI.put("nouvelleConstruction", "topmostSubform[0].Page4[0].C2ZA1_nouvelle[0]");
        champsFormPCMI.put("lotissement", "topmostSubform[0].Page3[0].T3I_lotoui[0]");
        this.champsForm.put(EnumTypes.PCMI.toString(), champsFormPCMI);
        Map<String, String> champsFormDPMI = new HashMap<String, String>();
        champsFormDPMI = new HashMap<String, String>();
        champsFormDPMI.put("numeroVoie", "topmostSubform[0].Page3[0].T2Q_numero[0]");
        champsFormDPMI.put("voie", "topmostSubform[0].Page3[0].T2V_voie[0]");
        champsFormDPMI.put("lieuDit", "topmostSubform[0].Page3[0].T2W_lieudit[0]");
        champsFormDPMI.put("codePostal", "topmostSubform[0].Page3[0].T2C_code[0]");
        champsFormDPMI.put("bp", "topmostSubform[0].Page3[0].T2B_boite[0]");
        champsFormDPMI.put("cedex", "topmostSubform[0].Page3[0].T2X_cedex[0]");
        champsFormDPMI.put("prefixe", "topmostSubform[0].Page3[0].T2F_prefixe[0]");
        champsFormDPMI.put("section", "topmostSubform[0].Page3[0].T2S_section[0]");
        champsFormDPMI.put("numeroCadastre", "topmostSubform[0].Page3[0].T2N_numero[0]");
        champsFormDPMI.put("nouvelleConstruction", "topmostSubform[0].Page3[0].C2ZA1_nouvelle[0]");
        champsFormDPMI.put("lotissement", "topmostSubform[0].Page3[0].T2J_lotissement[0]");
        this.champsForm.put(EnumTypes.DPMI.toString(), champsFormDPMI);
    }

    public String toNomChamp(EnumTypes type, String attribut) {
        String nomChamp = this.champsForm.get(type.toString()).get(attribut);
        log.debug("nomChamp(type={}, attribut={})={}", type, attribut, nomChamp);
        return nomChamp;
    }

    public Set<String> nomsChamps(EnumTypes type) {
        Set<String> nomsChamps = this.champsForm.get(type.toString()).keySet();
        log.debug("nomChamps(type={})={}", type, nomsChamps);
        return nomsChamps;
    }

}