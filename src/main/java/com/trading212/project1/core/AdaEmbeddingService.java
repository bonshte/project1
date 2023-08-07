package com.trading212.project1.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdaEmbeddingService {
    private static final int OK_CODE_RANGE_START = 200;
    private static final int OK_CODE_RANGE_END = 299;
    private static final Gson GSON = new Gson();
    private static final String ADA_MODEL = "text-embedding-ada-002";
    private static final String END_POINT = "https://api.openai.com/v1/embeddings";
    private static final String API_KEY = "your api key";
    public static void main(String[] args) {
        AdaEmbeddingService adaEmbeddingService = new AdaEmbeddingService();
        List<Float> floats = adaEmbeddingService.embedWithAda("i am a cool guy, and you are not as cool as i am");
        for (var f : floats) {
            System.out.println(f);
        }
    }
    public List<Float> embedWithAda(String text) {
        String adaRequestBody = generateAdaRequestBody(text);
        try {
            HttpResponse<String> response = sendPostRequest(END_POINT, API_KEY, adaRequestBody);
            validateResponse(response);
            return extractEmbedding(response);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private static HttpResponse<String> sendPostRequest(String apiUrl, String apiKey, String body)
            throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(new URI(apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
    private List<Float> extractEmbedding(HttpResponse<String> response) {
        String responseBody = response.body();
        JsonObject jsonObject = GSON.fromJson(responseBody, JsonObject.class);
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        if (dataArray.size() > 0) {
            JsonObject dataObject = dataArray.get(0).getAsJsonObject();
            JsonArray embeddingArray = dataObject.getAsJsonArray("embedding");
            List<Float> embeddingList = new ArrayList<>();
            for (int i = 0; i < embeddingArray.size(); i++) {
                embeddingList.add(embeddingArray.get(i).getAsFloat());
            }
            return embeddingList;
        } else {
            throw new IllegalArgumentException("Invalid JSON format: Missing or empty 'data' array.");
        }
    }
    private String generateAdaRequestBody(String text) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("input", text);
        jsonObject.addProperty("model", ADA_MODEL);
        return GSON.toJson(jsonObject);
    }
    private static void validateResponse(HttpResponse<String> response) throws RuntimeException {
        int statusCode = response.statusCode();
        String responseBody = response.body();
        if (statusCode < OK_CODE_RANGE_START || statusCode > OK_CODE_RANGE_END) {
            throw new RuntimeException("API request failed with status code: " +
                    statusCode + "\nResponse body: " + responseBody);
        }
    }
}