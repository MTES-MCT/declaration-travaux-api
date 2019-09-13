package com.github.mtesmct.rieau.api.infra.application.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithUserDetails;

@Retention(RetentionPolicy.RUNTIME)
@WithUserDetails(value = WithDeposantAndBetaDetails.ID, userDetailsServiceBeanName="userDetailsService")
public @interface WithDeposantAndBetaDetails {
    public final static String ID = "jean.martin";
}