package com.trading212.project1.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.trading212.project1.core.models.ChatMessage;
import com.trading212.project1.core.models.openai.GPT3Role;
import com.trading212.project1.core.models.openai.GPTFunctionCallDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openqa.selenium.json.TypeToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GPTService {
    private static final int OK_CODE_RANGE_START = 200;
    private static final int OK_CODE_RANGE_END = 299;
    private static final String API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String GPT_MODEL_LATEST = "gpt-3.5-turbo";
    private static final String GPT_MODEL_0301 = "gpt-3.5-turbo-0301";
    private static final String API_KEY = "your-api-key";

    private static final JsonParser JSON_PARSER = new JsonParser();

    public HttpResponse<String> processChat(List<GPTMessageDTO> oldMessages, String message) {
        if (oldMessages.isEmpty()) {
            GPTMessageDTO systemMessage = new GPTMessageDTO(GPT3Role.system, AGENT.RECOMMENDATION_AGENT_MESSAGE);
            oldMessages.add(systemMessage);
        }

        GPTMessageDTO userMessage = new GPTMessageDTO(GPT3Role.user, message);
        oldMessages.add(userMessage);
        try {
            String jsonBody = generateChatRequestBody(oldMessages, GPT_MODEL_LATEST);
            HttpResponse<String> response = sendPostRequest(API_ENDPOINT, API_KEY, jsonBody);
            validateResponse(response);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String summarizeDescription(String message) {
        GPTMessageDTO messageDTO = new GPTMessageDTO(GPT3Role.user, message);
        String requestBody = generateTranslationRequestBody(messageDTO, GPT_MODEL_LATEST);
        try {
            HttpResponse<String> response = sendPostRequest(API_ENDPOINT, API_KEY, requestBody);
            validateResponse(response);
            return extractContent(response.body()).content;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String summarizeRecommendations(List<GPTMessageDTO> oldMessages, String recommendations) {
        GPTMessageDTO systemMessage = new GPTMessageDTO(GPT3Role.system, recommendations);
        oldMessages.add(systemMessage);
        try {
            String jsonBody = generateChatRequestBody(oldMessages, GPT_MODEL_LATEST);
            HttpResponse<String> response = sendPostRequest(API_ENDPOINT, API_KEY, jsonBody);
            validateResponse(response);
            System.out.println(response.body());
            if (checkForFunctionCall(response.body())) {
                //sometimes the chat returns another function call after the system message and that is really annoying, requires a lot of code refactoring this is fast fix
                return "I am sending any matching properties";
            }
            return extractContent(response.body()).content;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GPTMessageDTO extractContent(String jsonString) {
        JsonObject jsonObject = JSON_PARSER.parse(jsonString).getAsJsonObject();
        JsonObject choiceObj = jsonObject.get("choices").getAsJsonArray().get(0).getAsJsonObject();
        JsonObject messageObj = choiceObj.get("message").getAsJsonObject();
        String role = messageObj.get("role").getAsString();
        String content = messageObj.get("content").getAsString();

        return new GPTMessageDTO(GPT3Role.valueOf(role), content);
    }

    public boolean checkForFunctionCall(String jsonString) {
        JsonObject jsonObject = JSON_PARSER.parse(jsonString).getAsJsonObject();
        JsonObject choiceObj = jsonObject.get("choices").getAsJsonArray().get(0).getAsJsonObject();
        JsonObject messageObj = choiceObj.get("message").getAsJsonObject();
        return messageObj.has("function_call");
    }

    public GPTFunctionCallDTO extractFunctionCall(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        JsonObject choiceObj = jsonObject.getAsJsonArray("choices").get(0).getAsJsonObject();
        JsonObject functionCallObj = choiceObj.getAsJsonObject("message").getAsJsonObject("function_call");
        String name = functionCallObj.get("name").getAsString();

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        String argumentsString = functionCallObj.get("arguments").getAsString();
        Map<String, Object> arguments = new Gson().fromJson(argumentsString, type);

        return new GPTFunctionCallDTO(name, arguments);
    }

    public List<GPTMessageDTO> toGPTMessageHistory(List<ChatMessage> sortedChatMessageHistory) {
        List<GPTMessageDTO> gptMessageHistory = new ArrayList<>();
        for (var chatMessage : sortedChatMessageHistory) {
            gptMessageHistory.add(
                    new GPTService.GPTMessageDTO(
                            (chatMessage.isFromUser() ? GPT3Role.user : GPT3Role.assistant),
                            chatMessage.getTranslatedMessage()
                    )
            );
        }
        return gptMessageHistory;
    }

    private  HttpResponse<String> sendPostRequest(String apiUrl, String apiKey, String jsonPayload)
            throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(new URI(apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }


    private  void validateResponse(HttpResponse<String> response) throws RuntimeException {
        int statusCode = response.statusCode();
        String responseBody = response.body();
        if (statusCode < OK_CODE_RANGE_START || statusCode > OK_CODE_RANGE_END) {
            throw new RuntimeException("API request failed with status code: " +
                    statusCode + "\nResponse body: " + responseBody);
        }
    }

    private String generateTranslationRequestBody(GPTMessageDTO message, String modelName) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", modelName);
        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", AGENT.DESCRIPTION_AGENT_SUMMARIZER);
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", message.getChatRole().toString());
        userMessage.addProperty("content", message.getContent());
        messages.add(userMessage);

        requestBody.add("messages", messages);

        return requestBody.toString();
    }


    private String generateChatRequestBody(List<GPTMessageDTO> messages, String modelName) {
        List<Map<String, String>> messagesInBody = new ArrayList<>();
        for (var message : messages) {
            Map bodyMessage = Map.of(
                    "role", message.getChatRole().toString(),
                    "content", message.getContent()
            );
            messagesInBody.add(bodyMessage);

        }

        Map<String, Object> functionParameterProperties = Map.of(
                "forRent", Map.of(
                        "type", "boolean",
                        "description", "true if the client is looking for apartment to rent, false if he is looking for apartment to buy"
                ),
                "town", Map.of(
                        "type", "string",
                        "description", "the desired city or the village by the user"
                ),
                "neighbourhoods", Map.of(
                        "type", "string",
                        "description", "the desired neighbourhoods by the user separated by ,"
                ),
                "apartmentType", Map.of(
                        "type", "string",
                        "enum", List.of("one room", "two room", "three room", "four room", "many room", "maisonette", "studio"),
                        "description", "the type of apartment"
                ),
                "price", Map.of(
                        "type", "integer",
                        "description", "the price for the apartment"
                ),
                "currency", Map.of(
                        "type", "string",
                        "description", "the currency for the apartment",
                        "enum", List.of("bgn", "euro")
                ),
                "features", Map.of(
                        "type", "string",
                        "description", "any additional features like pet-friendly, new furniture, view of the ocean, gym nearby etc. They should be split by ,"
                )

        );

        Map<String, Object> functionParameters = Map.of(
                "type", "object",
                "properties", functionParameterProperties,
                "required", List.of("town", "apartmentType", "forRent", "price", "currency")
        );

        Map<String, Object> function = Map.of(
                "name", "getRecommendations",
                "description", "get recommended properties for user requirements",
                "parameters", functionParameters
        );

        Map<String, Object> requestBody = Map.of(
                "model", modelName,
                "messages", messagesInBody,
                "temperature", 0.1,
                "functions", List.of(function),
                "function_call", "auto"
        );

        try {
            String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

            return jsonBody;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("problem with generating json body for request to GPT");
        }
    }




    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class GPTMessageDTO {
        private GPT3Role chatRole;
        private String content;
        @Override
        public String toString() {
            return "{\n" +
                    "  \"role\": \"" + chatRole.toString() + "\",\n" +
                    "  \"content\": \"" + content + "\"\n" +
                    "}";
        }
    }

    static class AGENT {
        private static final String DESCRIPTION_AGENT_SUMMARIZER = "You are agent who extracts information from apartment description. You want to extract only the features from the description such as proximity to stores, schools, buildings or new furniture, internet connection, security etc. You will not include any information about the broker, agency, phone numbers or advertisements of other properties. You will not include the price or size of the apartment. You will reply only with the extracted features in a informational sentences and you will not give any explanations. If no specific features were mentioned you will reply with \"no features\".";
        private static final String RECOMMENDATION_AGENT_MESSAGE = "You are an agent who gathers information from users about the desired apartment they want to rent or buy. You want to collect data for town (city or village), neighbourhoods, price, currency, type of apartment, additional features. You will guide the client in providing information about what apartment they want.You must explicitly collect the information about type of apartment and currency.When user wants the apartment to be near or close to something treat that as a feature.If the user provides a range for price or square meters take the average. You must information for town, currency and price before making function call. The only possible currencies are \"bgn\" and \"euro\". The allowed apartment types are only \"one room\", \"two room\", \"three room\", \"four room\", \"many room\", \"maisonette\" and \"studio\". You will not answer any questions unrelated to apartment search. Before making a function call to get recommended properties you will ask the client if the data you gatherer is what they want and proceed with function call only if they agree.";
    }
}
