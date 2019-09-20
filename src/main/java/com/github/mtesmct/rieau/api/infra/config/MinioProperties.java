package com.github.mtesmct.rieau.api.infra.config;

import java.net.URL;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("minio")
@Validated
@Profile("staging")
@Component
public class MinioProperties {
    @NotNull
    private URL endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private int buffer;
}