package com.github.mtesmct.rieau.api.infra.http;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithUserDetails;

@Retention(RetentionPolicy.RUNTIME)
@WithUserDetails("jean.martin")
public @interface WithDepositaireDetails {
}