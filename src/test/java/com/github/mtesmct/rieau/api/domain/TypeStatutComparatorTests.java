package com.github.mtesmct.rieau.api.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutComparator;
import com.github.mtesmct.rieau.api.domain.repositories.TypeStatutDossierRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TypeStatutComparatorTests {
    @Autowired
    private TypeStatutDossierRepository statutDossierRepository;
    private TypeStatutComparator typestatutComparator = new TypeStatutComparator();

    @Test
    public void compareQualifieDepose() {
        Optional<TypeStatut> typeDepose = this.statutDossierRepository.findById(EnumStatuts.DEPOSE);
        assertTrue(typeDepose.isPresent());
        Optional<TypeStatut> typeQualifie = this.statutDossierRepository.findById(EnumStatuts.QUALIFIE);
        assertTrue(typeQualifie.isPresent());
        assertTrue(this.typestatutComparator.compare(typeDepose.get(), typeQualifie.get()) < 0);
    }

    @Test
    public void compareInstructionIncomplet() {
        Optional<TypeStatut> typeInstruction = this.statutDossierRepository.findById(EnumStatuts.INSTRUCTION);
        assertTrue(typeInstruction.isPresent());
        Optional<TypeStatut> typeIncomplet = this.statutDossierRepository.findById(EnumStatuts.INCOMPLET);
        assertTrue(typeIncomplet.isPresent());
        assertTrue(this.typestatutComparator.compare(typeInstruction.get(), typeIncomplet.get()) > 0);
    }
    @Test
    public void compareInstructionInstruction() {
        Optional<TypeStatut> typeInstruction = this.statutDossierRepository.findById(EnumStatuts.INSTRUCTION);
        assertTrue(typeInstruction.isPresent());
        assertTrue(this.typestatutComparator.compare(typeInstruction.get(), typeInstruction.get()) == 0);
    }

}