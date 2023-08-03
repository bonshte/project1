package com.trading212.project1.api.rest.models;

import com.trading212.project1.core.models.ChatMessage;
import com.trading212.project1.core.models.ChatSession;

public class Mappers {
    private Mappers() {
        throw new RuntimeException("should not be instantiated");
    }

    public static ChatMessageResponse fromChatMessage(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getSentMessage(),
                chatMessage.isFromUser(),
                chatMessage.getChatSessionId()
        );
    }

    public static ChatSessionResponse fromChatSession(ChatSession chatSession) {
        return new ChatSessionResponse(
                chatSession.getSessionId(),
                chatSession.getDescription()
        );
    }

}
