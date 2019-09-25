package com.github.mtesmct.rieau.api.infra.config;

import java.net.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("minio")
@Validated
@Component
public class MinioProperties {
    private boolean enabled;
    private URL endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private int buffer;
}