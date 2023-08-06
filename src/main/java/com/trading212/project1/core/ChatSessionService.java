package com.trading212.project1.core;

import com.trading212.project1.core.mappers.Mappers;
import com.trading212.project1.core.models.ChatMessage;
import com.trading212.project1.core.models.ChatSession;
import com.trading212.project1.repositories.ChatRepository;
import com.trading212.project1.repositories.entities.ChatMessageEntity;
import com.trading212.project1.repositories.entities.ChatSessionEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ChatSessionService {
    private final ChatRepository chatRepository;

    public ChatSessionService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<ChatSession> getUserChatSessions(int userId) {

        return chatRepository.getChatSessionsForUser(userId)
            .stream()
            .map(Mappers::fromChatSessionEntity)
            .sorted(Comparator.comparing(ChatSession::getSessionId).reversed())
            .toList();
    }

    public ChatSession getChatSession(int sessionId) {
        ChatSessionEntity chatSessionEntity = chatRepository.getChatSession(sessionId).orElseThrow();
        return Mappers.fromChatSessionEntity(chatSessionEntity);
    }


    public ChatSession createSession(int userId, String description) {
        return Mappers.fromChatSessionEntity(chatRepository.createChatSession(userId, description));
    }
}
