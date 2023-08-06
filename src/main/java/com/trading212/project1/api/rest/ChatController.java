package com.trading212.project1.api.rest;

import com.trading212.project1.api.rest.models.ChatHistoryResponse;
import com.trading212.project1.api.rest.models.ChatMessageResponse;
import com.trading212.project1.api.rest.models.Mappers;
import com.trading212.project1.api.rest.models.input.MessageInput;
import com.trading212.project1.api.rest.models.SessionsResponse;
import com.trading212.project1.core.ChatService;
import com.trading212.project1.core.ChatSessionService;
import com.trading212.project1.core.models.ChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/properties-chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatSessionService chatSessionService;

    public ChatController(ChatService chatService, ChatSessionService chatSessionService) {
        this.chatService = chatService;
        this.chatSessionService = chatSessionService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SessionsResponse> getUserChatSessions(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(
            new SessionsResponse(
                chatSessionService.getUserChatSessions(userId)
                        .stream()
                        .map(Mappers::fromChatSession)
                        .toList()
            )
        );
    }

    @GetMapping("/{userId}/{sessionId}")
    public ResponseEntity<ChatHistoryResponse> getMessageHistory(
        @PathVariable("userId") int userId,
        @PathVariable("sessionId") int sessionId
    ) {
        var sessionMessageHistory = chatService.getChatSessionMessageHistory(userId, sessionId)
                .stream()
                    .map(Mappers::fromChatMessage)
                        .toList();
        return ResponseEntity.ok(
            new ChatHistoryResponse(
                sessionMessageHistory
            )
        );
    }



    @PostMapping("/{userId}/{sessionId}")
    public ResponseEntity<ChatMessageResponse> receiveMessage(
        @PathVariable("userId") int userId,
        @PathVariable("sessionId") int sessionId,
        @RequestBody MessageInput messageInput) {
        return ResponseEntity.ok(
                Mappers.fromChatMessage(chatService.processUserMessage(userId, sessionId, messageInput.getMessage()))
        );
    }
}
