package com.github.mtesmct.rieau.api.depositaire.infra.config;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateFormatConfig {

    @Bean(name = "dateTimeConverter")
    public DateConverter dateTimeConverter(@Value("${app.datetime.format}") String format){
        return new DateConverter(format);
    }

    @Bean(name = "dateConverter")
    public DateConverter dateConverter(@Value("${app.date.format}") String format){
        return new DateConverter(format);
    }
 
    @Bean
    @ConditionalOnProperty(value = "spring.jackson.date-format", matchIfMissing = true, havingValue = "none")
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer(@Value("${app.datetime.format}") String dateTimeFormat,@Value("${app.date.format}") String dateFormat) {
        return builder -> {
            builder.simpleDateFormat(dateTimeFormat);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        };
    }
 
}