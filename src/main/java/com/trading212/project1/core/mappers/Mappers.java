package com.trading212.project1.core.mappers;

import com.trading212.project1.core.models.ChatMessage;
import com.trading212.project1.core.models.ChatSession;
import com.trading212.project1.core.models.User;
import com.trading212.project1.repositories.entities.ChatMessageEntity;
import com.trading212.project1.repositories.entities.ChatSessionEntity;
import com.trading212.project1.repositories.entities.UserEntity;

public class Mappers {
    public static User fromUserEntity(UserEntity userEntity) {
        return new User(
            userEntity.getId(),
            userEntity.getEmail(),
            userEntity.getPassword(),
            userEntity.getPremiumUntil(),
            userEntity.getRole()
        );
    }

    public static ChatMessage fromChatMessageEntity(ChatMessageEntity chatMessageEntity) {
        return new ChatMessage(
            chatMessageEntity.getChatSessionId(),
            chatMessageEntity.getSentMessage(),
            chatMessageEntity.getTranslatedMessage(),
            chatMessageEntity.isFromUser(),
            chatMessageEntity.getTimeSent()
        );
    }

    public static ChatSession fromChatSessionEntity(ChatSessionEntity chatSessionEntity) {
        return new ChatSession(
            chatSessionEntity.getSessionId(),
            chatSessionEntity.getDescription(),
            chatSessionEntity.getUserId()
        );
    }
}
