package com.trading212.project1.repositories;

import com.trading212.project1.repositories.entities.ChatMessageEntity;
import com.trading212.project1.repositories.entities.ChatSessionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRepository {

    List<ChatSessionEntity> getChatSessionsForUser(int userId);
    ChatSessionEntity createChatSession(int userId, String description);

    List<ChatMessageEntity> getChatSessionMessages(int sessionId);

    ChatMessageEntity createMessage(int sessionId, String sentMessage,
                                    String translatedMessage, boolean fromUser, LocalDateTime timestamp);
    Optional<ChatSessionEntity> getChatSession(int sessionId);

}
