package com.github.mtesmct.rieau.api.infra.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
@ConditionalOnProperty(prefix = "keycloak", name = "enabled", havingValue = "true")
public class KeycloakConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.ssl-required}")
    private String sslRequired;
    @Value("${keycloak.resource}")
    private String resource;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.use-resource-role-mappings}")
    private boolean useResourceRoleMappings;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy()).and()
                .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
                .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and().logout()
                .addLogoutHandler(keycloakLogoutHandler()).logoutUrl("/sso/logout")
                .logoutSuccessHandler((HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                .and().apply(new CommonHttpConfigurer());
    }

    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakConfigResolver() {
            private KeycloakDeployment keycloakDeployment;

            @Override
            public KeycloakDeployment resolve(HttpFacade.Request facade) {
                if (keycloakDeployment != null) {
                    return keycloakDeployment;
                }
                AdapterConfig adapterConfig = new AdapterConfig();
                adapterConfig.setAuthServerUrl(authServerUrl);
                adapterConfig.setRealm(realm);
                adapterConfig.setResource(resource);
                adapterConfig.setSslRequired(sslRequired);
                adapterConfig.setUseResourceRoleMappings(useResourceRoleMappings);
                keycloakDeployment = KeycloakDeploymentBuilder.build(adapterConfig);
                return keycloakDeployment;
            }
        };
    }
}