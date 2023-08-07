package com.trading212.project1.core;

import com.github.pemistahl.lingua.api.Language;
import com.trading212.project1.core.exceptions.UnauthorizedException;
import com.trading212.project1.core.mappers.Mappers;
import com.trading212.project1.core.models.*;
import com.trading212.project1.core.models.openai.GPT3Role;
import com.trading212.project1.core.models.openai.GPTFunctionCallDTO;
import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Currency;
import com.trading212.project1.repositories.ChatRepository;
import com.trading212.project1.repositories.entities.ChatMessageEntity;
import com.trading212.project1.repositories.entities.ChatSessionEntity;
import com.trading212.project1.repositories.milvus.MilvusAdsRepository;
import org.checkerframework.checker.units.qual.C;
import org.json.HTTP;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatSessionService chatSessionService;
    private final AuthorizationService authorizationService;
    private final LanguageDetectionService languageDetectionService;
    private final DeepLTranslationService translationService;
    private final GPTService gptService;
    private final AdaEmbeddingService embeddingService;
    private final AdService adService;

    private final RecommendationService recommendationService;
    private static final String WELCOME_MESSAGE = "Hello! What apartment are you looking for?";

    public ChatService(ChatRepository chatRepository, ChatSessionService chatSessionService,
                       AuthorizationService authorizationService, LanguageDetectionService languageDetectionService,
                       DeepLTranslationService translationService, GPTService gptService,
                       AdaEmbeddingService embeddingService, AdService adService,
                       RecommendationService recommendationService) {
        this.chatRepository = chatRepository;
        this.chatSessionService = chatSessionService;
        this.authorizationService = authorizationService;
        this.languageDetectionService = languageDetectionService;
        this.translationService = translationService;
        this.gptService = gptService;
        this.embeddingService = embeddingService;
        this.adService = adService;
        this.recommendationService = recommendationService;
    }

    @Transactional
    public ChatMessage processUserMessage(int userId, int sessionId, String message) {
        //ako nyama sesia ya suzdavam
        if (sessionId == 0) {
            if (!authorizationService.isAuthorizedUserDataRequest(userId)) {
                throw new UnauthorizedException("access denied");
            }
            var chatSession = chatSessionService.createSession(userId, message);
            sessionId = chatSession.getSessionId();
            chatRepository.createMessage(sessionId, WELCOME_MESSAGE, WELCOME_MESSAGE,
                false, LocalDateTime.now(), false);
        } else {
            if (!authorizationService.isAuthorizedUserSessionRequest(userId, sessionId)) {
                throw new UnauthorizedException("access denied");
            }
        }
        Language detectedLanguage = languageDetectionService.detectLanguage(message);

        //prevejdam suobshtenieto v angliski za gpt
        String messageForGPTChat = message;
        if (detectedLanguage != Language.ENGLISH && translationService.isTranslateSupported(detectedLanguage)) {
            messageForGPTChat = translationService.translateToEnglish(message);
        }

        //suzdavam si suobshtenieto koeto e ot potrebitelya
        ChatMessage userMessage = new ChatMessage();
        userMessage.setChatSessionId(sessionId);
        userMessage.setSentMessage(message);
        userMessage.setTranslatedMessage(messageForGPTChat);
        userMessage.setFromUser(true);
        userMessage.setTimeSent(LocalDateTime.now());


        List<ChatMessage> oldChatMessages = getChatSessionMessages(sessionId);

        List<ChatMessage> sortedChatMessages = oldChatMessages.stream()
            .sorted(Comparator.comparing(ChatMessage::getTimeSent)
                .thenComparing(ChatMessage::isFromUser, Comparator.reverseOrder()))
            .toList();

        //suzdavam si istoriyata ot samo gpt suobshteniyata
        List<GPTService.GPTMessageDTO> oldChatHistoryWithGPT = new ArrayList<>();
        for (var chatMessage : sortedChatMessages) {
            oldChatHistoryWithGPT.add(
                new GPTService.GPTMessageDTO(
                    (chatMessage.isFromUser() ? GPT3Role.user : GPT3Role.assistant),
                    chatMessage.getTranslatedMessage()
                )
            );
        }

        //pitam chata s novoto suobshtenie
        HttpResponse<String> gptResponse = gptService.processChat(oldChatHistoryWithGPT, messageForGPTChat);

        //ako ima function call
        if (gptService.checkForFunctionCall(gptResponse.body())) {

            GPTFunctionCallDTO functionCallDTO = gptService.extractFunctionCall(gptResponse.body());


            List<Ad> recommendations = getRecommendations(functionCallDTO.getArguments());

            if (recommendations.isEmpty()) {
                String messageForGPT = "Sorry, I found nothing that matches your description";
                String messageForUser = messageForGPT;
                if (detectedLanguage != Language.ENGLISH && translationService.isTranslateSupported(detectedLanguage)) {
                    messageForGPT = translationService.translateToLanguage(messageForGPT, detectedLanguage);
                }
                ChatMessage botMessage = new ChatMessage(
                    sessionId,
                    messageForUser,
                    messageForGPT,
                    false,
                    LocalDateTime.now(),
                    false
                );

                saveConversationCompletion(userMessage, botMessage);
                return botMessage;
            }
            //suzdvam tezi rekomendacii
            recommendationService.createRecommendations(recommendations, userId, sessionId);
            GPTService.GPTMessageDTO userMessageToGPT = new GPTService.GPTMessageDTO(
                GPT3Role.user,
                messageForGPTChat
            );

            //vzimam starata istorya s gpt
            List<GPTService.GPTMessageDTO> currentMessageHistory = new ArrayList<>(oldChatHistoryWithGPT);
            currentMessageHistory.add(userMessageToGPT);

            //pitam go da summarize poluchenite  recomendacii
            List<String> summaries = new ArrayList<>();
            for (var recommendation : recommendations) {
                summaries.add(recommendation.getSummary());
            }

            String summaryResponseFromGPT = gptService.summarizeRecommendations(currentMessageHistory,
                "summarise matching properties found:" + summaries);
            String summaryForUser = summaryResponseFromGPT;

            //prevejdam v pravilen ezik poluchenoto summary
            if (detectedLanguage != Language.ENGLISH && translationService.isTranslateSupported(detectedLanguage)) {
                summaryForUser = translationService.translateToLanguage(summaryForUser, detectedLanguage);
            }

            //
            ChatMessage botMessage = new ChatMessage();
            botMessage.setChatSessionId(sessionId);
            botMessage.setSentMessage(summaryForUser);
            botMessage.setTranslatedMessage(summaryResponseFromGPT);
            botMessage.setFromUser(false);
            botMessage.setTimeSent(LocalDateTime.now());
            botMessage.setAdsFound(true);

            saveConversationCompletion(userMessage, botMessage);
            return botMessage;
        } else {

            String gptResponseString = gptService.extractContent(gptResponse.body()).getContent();
            String messageForUser = gptResponseString;

            if (detectedLanguage != Language.ENGLISH && translationService.isTranslateSupported(detectedLanguage)) {
                messageForUser = translationService.translateToLanguage(messageForUser, detectedLanguage);
            }

            ChatMessage botMessage = new ChatMessage();
            botMessage.setChatSessionId(sessionId);
            botMessage.setSentMessage(messageForUser);
            botMessage.setTranslatedMessage(gptResponseString);
            botMessage.setFromUser(false);
            botMessage.setTimeSent(LocalDateTime.now());


            saveConversationCompletion(userMessage, botMessage);
            return botMessage;
        }
    }

    public List<ChatMessage> getChatSessionMessageHistory(int userId, int chatSessionId) {
        if (!authorizationService.isAuthorizedUserSessionRequest(userId, chatSessionId)) {
            throw new UnauthorizedException("can not access data for another user");
        }
        List<ChatMessage> sessionMessageHistory =  chatRepository.getChatSessionMessages(chatSessionId)
            .stream()
            .map(Mappers::fromChatMessageEntity)
            .toList();


        return sessionMessageHistory;
    }



    private List<Ad> getRecommendations(Map<String, Object> args) {
        System.out.println("the function call args");
        System.out.println(args);
        UserRequirement userRequirement = generateUserRequirement(args);
        System.out.println("the user requirements object");
        System.out.println(userRequirement);
        String embeddableUserRequirements = userRequirement.toEmbeddableText();
        System.out.println("the embeddable user requirements:");
        System.out.println(embeddableUserRequirements);
        List<Float> embeddedRequirements = embeddingService.embedWithAda(embeddableUserRequirements);
        SimilaritySearchFilter similaritySearchFilter = new SimilaritySearchFilter(
            userRequirement.getTown(),
            userRequirement.getPrice() != null ? userRequirement.priceInBGN() : null,
            userRequirement.getAccommodationType().toString()
        );
        System.out.println("the similarity search filter");
        System.out.println(similaritySearchFilter);
        List<Ad> recommendations = adService.findClosestAds(
            embeddedRequirements,
            userRequirement.isForSale() ? MilvusAdsRepository.SALE_PARTITION : MilvusAdsRepository.RENT_PARTITION,
            5,
            similaritySearchFilter
            );
        System.out.println("found recommendations:");
        for (var rec : recommendations) {
            System.out.println(rec);
        }
        return recommendations;
    }

    private UserRequirement generateUserRequirement(Map<String, Object> args) {
        AccommodationType accommodationType = null;
        if (args.get("apartmentType") != null) {
            accommodationType =
                AccommodationType.fromGPTFunctionArgument((String) args.get("apartmentType"));
        }

        Integer price = null;
        if (args.get("price") != null) {

            price = ((Number) args.get("price")).intValue();
            if (price == 0) {
                price = null;
            }
        }

        List<String> neighbourhoods = new ArrayList<>();
        if (args.get("neighbourhoods") != null) {
            String neighbourhoodsString = (String) args.get("neighbourhoods");
            neighbourhoods = Arrays.asList(neighbourhoodsString.split("\\s*,\\s*"));
        }

        List<String> features = new ArrayList<>();
        if (args.get("features") != null) {
            String featuresString = (String) args.get("features");
            features = Arrays.asList(featuresString.split("\\s*,\\s*"));
        }


        boolean forSale = !((boolean) args.get("forRent"));
        String town = null;
        if (args.get("town") != null) {
            town = (String) args.get("town");
        }


        Currency currency = Currency.BGN;
        if (args.get("currency") != null) {
            currency = Currency.fromGPTFunctionArgument((String) args.get("currency"));
        }

        return new UserRequirement(
            town,
            currency,
            price,
            accommodationType,
            neighbourhoods,
            features,
            forSale
        );
    }




    @Transactional
    private void saveConversationCompletion(ChatMessage userMessage, ChatMessage botMessage) {
        createMessage(userMessage);
        createMessage(botMessage);
    }

    private List<ChatMessage> getChatSessionMessages(int chatSessionId) {
        List<ChatMessageEntity> chatMessageEntities =
            chatRepository.getChatSessionMessages(chatSessionId);
        return chatMessageEntities.stream()
           .map(Mappers::fromChatMessageEntity).toList();
    }


    private void createMessage(ChatMessage message) {
        chatRepository.createMessage(
            message.getChatSessionId(),
            message.getSentMessage(),
            message.getTranslatedMessage(),
            message.isFromUser(),
            message.getTimeSent(),
            message.isAdsFound()
        );

    }
}
