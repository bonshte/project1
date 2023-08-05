package com.trading212.project1.configuration;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LanguageDetectionBeans {

    @Bean
    public LanguageDetector getLanguageDetector() {
        return LanguageDetectorBuilder.fromAllLanguages().build();
    }
}
