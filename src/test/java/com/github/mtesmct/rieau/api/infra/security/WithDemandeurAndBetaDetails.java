package com.github.mtesmct.rieau.api.infra.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithUserDetails;

@Retention(RetentionPolicy.RUNTIME)
@WithUserDetails(value = "jean.martin", userDetailsServiceBeanName="userDetailsService")
public @interface WithDemandeurAndBetaDetails {
}