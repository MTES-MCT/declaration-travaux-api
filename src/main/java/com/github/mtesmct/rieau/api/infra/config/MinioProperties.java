package com.github.mtesmct.rieau.api.infra.config;

import java.net.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    @NotNull
    private URL endpoint;
    @NotBlank
    private String accessKey;
    @NotBlank
    private String secretKey;
    @NotBlank
    private String bucket;
    private int buffer;
    @NotBlank
    private String filesOutputDir;
}