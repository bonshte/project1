package com.trading212.project1.core;

import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import org.springframework.stereotype.Service;

@Service
public class LanguageDetectionService {
    private final LanguageDetector languageDetector;

    public LanguageDetectionService(LanguageDetector languageDetector) {
        this.languageDetector = languageDetector;
    }

    public Language detectLanguage(String text) {
        return languageDetector.detectLanguageOf(text);
    }
}
