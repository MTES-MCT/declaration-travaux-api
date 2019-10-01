package com.github.mtesmct.rieau.api.infra.config;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.UsersService;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.infra.application.auth.WithAutreDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockUsersConfig {

    @Autowired
    private UsersService userService;

    @Bean
    @Qualifier("deposantBeta")
    public Personne deposantBeta(){
        Optional<Personne> deposantBeta = this.userService.findUserById((WithDeposantBetaDetails.ID));
        if (deposantBeta.isEmpty())
            throw new NullPointerException("Le bean de l'utilisateur déposant beta ne peut être instantié car le user service ne le trouve pas.");
        return deposantBeta.get();
    }
    @Bean
    @Qualifier("autreDeposantBeta")
    public Personne autreDeposantBeta(){
        Optional<Personne> autreDeposantBeta = this.userService.findUserById((WithAutreDeposantBetaDetails.ID));
        if (autreDeposantBeta.isEmpty())
            throw new NullPointerException("Le bean de l'utilisateur autre déposant beta ne peut être instantié car le user service ne le trouve pas.");
        return autreDeposantBeta.get();
    }

    @Bean
    @Qualifier("instructeurNonBeta")
    public Personne instructeurNonBeta(){
        Optional<Personne> instructeurNonBeta = this.userService.findUserById(WithInstructeurNonBetaDetails.ID);
        if (instructeurNonBeta.isEmpty())
            throw new NullPointerException("Le bean de l'utilisateur instructeur non beta ne peut être instantié car le user service ne le trouve pas.");
        return instructeurNonBeta.get();
    }

}