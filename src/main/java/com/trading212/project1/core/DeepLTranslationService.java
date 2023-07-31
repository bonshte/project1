package com.trading212.project1.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeepLTranslationService {
    private static final String DEEPL_END_POINT = "https://api-free.deepl.com/v2/translate";
    private static final String API_KEY = "5a53ed53-d5d3-5298-a1c3-39db9275eaa6:fx";
    private static final String ENG_LANG_CODE = "EN";
    private static final int OK_CODE_RANGE_START = 200;
    private static final int OK_CODE_RANGE_END = 299;

    public String translateToEnglish(String text) {
        try {
            HttpResponse<String> response = sendTranslationRequest(text, ENG_LANG_CODE);
            handleResponse(response);
            return extractTranslation(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> translateToEnglish(List<String> sentences) {
        List<String> translations = new ArrayList<>();
        for (var sentence : sentences) {
            translations.add(translateToEnglish(sentence));
        }
        return translations;
    }

    private String extractTranslation(HttpResponse<String> response) {
        String responseBody = response.body();
        JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
        JsonArray translationsArray = jsonObject.getAsJsonArray("translations");
        JsonObject firstTranslationObject = translationsArray.get(0).getAsJsonObject();

        return firstTranslationObject.get("text").getAsString();
    }

    private HttpResponse<String> sendTranslationRequest(String text, String targetLang) throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String bodyData = generateRequestBody(text, targetLang);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DEEPL_END_POINT))
                .header("Authorization", "DeepL-Auth-Key " + API_KEY)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(bodyData)) // Use BodyPublishers.ofString() to send the data
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void handleResponse(HttpResponse<String> response) {
        if (response.statusCode() < OK_CODE_RANGE_START || response.statusCode() > OK_CODE_RANGE_END ) {
            throw new RuntimeException("bad response code from deepL");
        }
    }

    private String generateRequestBody(String text, String targetLang) {
        return "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8)
                + "&target_lang=" + URLEncoder.encode(targetLang, StandardCharsets.UTF_8);
    }


    public static void main(String[] args) {
        String textToTranslate = "търся си готин апартамент за 500 евро на месец в Драгалевци близо до метро";
        DeepLTranslationService main = new DeepLTranslationService();
        System.out.println(main.translateToEnglish(textToTranslate));
    }
}
