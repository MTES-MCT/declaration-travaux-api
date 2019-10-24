package com.github.mtesmct.rieau.api.infra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig {

	private final AppProperties properties;

	@Autowired
	public CORSConfig(AppProperties properties) {
		this.properties = properties;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods(HttpMethod.GET.toString(), HttpMethod.HEAD.toString(), HttpMethod.POST.toString(), HttpMethod.DELETE.toString()).allowedOrigins(properties.getCorsAllowedOrigins()).allowedHeaders("*")
						.allowCredentials(true).exposedHeaders("Content-Disposition");
			}
		};
	}
}