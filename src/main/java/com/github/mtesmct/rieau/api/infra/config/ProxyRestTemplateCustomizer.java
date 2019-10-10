package com.github.mtesmct.rieau.api.infra.config;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${app.proxy-host:}')")
@Slf4j
public class ProxyRestTemplateCustomizer implements RestTemplateCustomizer {

    @Autowired
    private AppProperties properties;

    @Override
    public void customize(RestTemplate restTemplate) {
        HttpHost proxy = new HttpHost(this.properties.getProxyHost(), this.properties.getProxyPort(), this.properties.getProxyScheme());
        log.info("Le proxy HTTP utilisé est: {}", proxy.toURI());
        HttpClient httpClient = HttpClientBuilder.create().setRoutePlanner(new DefaultProxyRoutePlanner(proxy) {

            @Override
            public HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context)
                    throws HttpException {
                return super.determineProxy(target, request, context);
            }

        }).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        log.info("Le timeout des connexions HTTP utilisé est de {} ms", this.properties.getHttpTimeout());
        requestFactory.setConnectTimeout(this.properties.getHttpTimeout());
        requestFactory.setConnectionRequestTimeout(this.properties.getHttpTimeout());
        requestFactory.setReadTimeout(this.properties.getHttpTimeout());
        restTemplate.setRequestFactory(requestFactory);
    }

}