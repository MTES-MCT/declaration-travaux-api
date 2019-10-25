package com.github.mtesmct.rieau.api.infra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.URL;

@Getter
@Setter
@ConfigurationProperties("minio")
@Validated
@Component
public class MinioProperties {
    @NotNull
    private boolean enabled;
    private URL endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private int buffer;
    private String filesOutputDir;
}