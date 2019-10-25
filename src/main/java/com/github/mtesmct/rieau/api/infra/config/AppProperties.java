package com.github.mtesmct.rieau.api.infra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ConfigurationProperties("app")
@Validated
@Component
public class AppProperties {
    @NotNull
    private String dateFormat;
    @NotNull
    private String datetimeFormat;
    @NotNull
    private String allDatetimeFormat;
    @NotNull
    private String yearFormat;
    private String fichiersDir;
    private String corsAllowedOrigins;
    private String communesUrl;
    private Integer httpTimeout;
    private String proxyHost;
    private String proxyScheme;
    private Integer proxyPort;
}