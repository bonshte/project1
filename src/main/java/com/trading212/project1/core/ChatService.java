package com.trading212.project1.core;

import com.github.pemistahl.lingua.api.Language;
import com.trading212.project1.core.exceptions.UnauthorizedException;
import com.trading212.project1.core.mappers.Mappers;
import com.trading212.project1.core.models.ChatMessage;
import com.trading212.project1.core.models.ChatSession;
import com.trading212.project1.core.models.User;
import com.trading212.project1.core.models.openai.GPT3Role;
import com.trading212.project1.repositories.ChatRepository;
import com.trading212.project1.repositories.entities.ChatMessageEntity;
import com.trading212.project1.repositories.entities.ChatSessionEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final LanguageDetectionService languageDetectionService;
    private final DeepLTranslationService translationService;
    private final GPTService gptService;
    private final AdaEmbeddingService embeddingService;

    public ChatService(ChatRepository chatRepository, LanguageDetectionService languageDetectionService,
                       DeepLTranslationService translationService, GPTService gptService,
                       AdaEmbeddingService embeddingService) {
        this.chatRepository = chatRepository;
        this.languageDetectionService = languageDetectionService;
        this.translationService = translationService;
        this.gptService = gptService;
        this.embeddingService = embeddingService;
    }



    public ChatMessage processUserMessage(int userId, int sessionId, String message) {
        if (sessionId == 0) {
            isSelfDataRequest(userId);
            var chatSession = createSession(userId, message);
            sessionId = chatSession.getSessionId();
        } else {
            if (!isSelfDataRequest(userId, sessionId)) {
                throw new UnauthorizedException("access denied");
            }
        }
        Language detectedLanguage = languageDetectionService.detectLanguage(message);

        String translatedMessage = message;
        if (detectedLanguage != Language.ENGLISH && translationService.isTranslateSupported(detectedLanguage)) {
            translatedMessage = translationService.translateToEnglish(message);
        }

        ChatMessage userMessage = new ChatMessage(
            sessionId,
            message,
            translatedMessage,
            true,
            LocalDateTime.now()
        );

        List<ChatMessage> oldChatMessages = getChatSessionMessages(sessionId);
        List<ChatMessage> sortedChatMessages = oldChatMessages.stream()
            .sorted(Comparator.comparing(ChatMessage::getTimeSent)
                .thenComparing(ChatMessage::isFromUser, Comparator.reverseOrder()))
            .toList();

        List<GPTService.GPTMessageDTO> chatHistoryWithGPT = new ArrayList<>();
        for (var chatMessage : sortedChatMessages) {
            chatHistoryWithGPT.add(
                new GPTService.GPTMessageDTO(
                    (chatMessage.isFromUser() ? GPT3Role.user : GPT3Role.assistant),
                    chatMessage.getTranslatedMessage()
                )
            );
        }

        HttpResponse<String> gptResponse = gptService.processChat(chatHistoryWithGPT, translatedMessage);

        if (gptService.checkForFunctionCall(gptResponse.body())) {
            System.out.println(gptService.extractFunctionCall(gptResponse.body()));
            return null;
            //here we continue as if function was called to search for the apartment
        } else {

            String gptResponseString = gptService.extractContent(gptResponse.body()).getContent();
            String messageForUser = gptResponseString;
            //fix that for all languages

            if (detectedLanguage != Language.ENGLISH && translationService.isTranslateSupported(detectedLanguage)) {
                messageForUser = translationService.translateToLanguage(messageForUser, detectedLanguage);
            }

            ChatMessage botMessage = new ChatMessage(
                sessionId,
                messageForUser,
                gptResponseString,
                false,
                LocalDateTime.now()
            );
            saveConversationCompletion(userMessage, botMessage);
            return botMessage;
        }
    }

    public List<ChatMessage> getChatSessionMessageHistory(int userId, int chatSession) {
        if (!isSelfDataRequest(userId, chatSession)) {
            throw new UnauthorizedException("can not access data for another user");
        }
        return chatRepository.getChatSessionMessages(chatSession)
            .stream()
            .map(Mappers::fromChatMessageEntity)
            .toList();
    }

    public List<ChatSession> getUserChatSessions(int userId) {
        if (!isSelfDataRequest(userId)) {
            throw new UnauthorizedException("can not access data for another user");
        }
        return chatRepository.getChatSessionsForUser(userId)
            .stream()
            .map(Mappers::fromChatSessionEntity)
            .sorted(Comparator.comparing(ChatSession::getSessionId).reversed())
            .toList();
    }

    private List<ChatSession> getChatSessionsForUser(int userId) {
        List<ChatSessionEntity> chatSessionEntities =
            chatRepository.getChatSessionsForUser(userId);
        return chatSessionEntities.stream()
            .map(Mappers::fromChatSessionEntity)
            .toList();
    }



    private boolean isSelfDataRequest(int userId, int sessionId) {
        ChatSession chatSession = getChatSession(sessionId);
        return isSelfDataRequest(userId) && userId == chatSession.getUserId();
    }

    private boolean isSelfDataRequest(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userId == user.getId();
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

    private ChatSession getChatSession(int sessionId) {
        ChatSessionEntity chatSessionEntity = chatRepository.getChatSession(sessionId).orElseThrow();
        return Mappers.fromChatSessionEntity(chatSessionEntity);
    }




    private ChatSession createSession(int userId, String description) {
        return Mappers.fromChatSessionEntity(chatRepository.createChatSession(userId, description));
    }

    private ChatMessage createMessage(ChatMessage message) {
        return Mappers.fromChatMessageEntity(chatRepository.createMessage(
            message.getChatSessionId(),
            message.getSentMessage(),
            message.getTranslatedMessage(),
            message.isFromUser(),
            message.getTimeSent()
        ));
    }
}
