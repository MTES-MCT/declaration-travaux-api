package com.github.mtesmct.rieau.api.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Statut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutComparator;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.repositories.TypeStatutDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StatutComparatorTests {
    @Autowired
    private DateService dateService;
    @Autowired
    private TypeStatutDossierRepository statutDossierRepository;

    @Test
    public void compareQualifieDepose() {
        StatutComparator statutComparator = new StatutComparator();
        Optional<TypeStatut> typeDepose = this.statutDossierRepository.findById(EnumStatuts.DEPOSE);
        assertTrue(typeDepose.isPresent());
        Statut statutDepose = new Statut(typeDepose.get(), this.dateService.now());
        Optional<TypeStatut> typeQualifie = this.statutDossierRepository.findById(EnumStatuts.QUALIFIE);
        assertTrue(typeQualifie.isPresent());
        Statut statutQualifie = new Statut(typeQualifie.get(), this.dateService.now());
        assertTrue(statutComparator.compare(statutDepose, statutQualifie) < 0);
    }

}