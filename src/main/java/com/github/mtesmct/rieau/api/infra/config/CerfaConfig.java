package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.application.NoNationalService;
import com.github.mtesmct.rieau.api.domain.adapters.CerfaAdapter;
import com.github.mtesmct.rieau.api.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.infra.file.pdf.PdfCerfaAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CerfaConfig {

	@Autowired
    private DateRepository dateRepository;
	@Autowired
    private NoNationalService noNationalService;

    @Bean
    public CerfaAdapter cerfaAdapter(){
        return new PdfCerfaAdapter(this.dateRepository, this.noNationalService);
    }
}
