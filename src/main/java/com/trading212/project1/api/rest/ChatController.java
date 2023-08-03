package com.trading212.project1.api.rest;

import com.trading212.project1.api.rest.models.ChatHistoryResponse;
import com.trading212.project1.api.rest.models.MessageInput;
import com.trading212.project1.api.rest.models.MessageResponse;
import com.trading212.project1.api.rest.models.SessionsResponse;
import com.trading212.project1.core.ChatService;
import com.trading212.project1.core.models.ChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat-properties")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @GetMapping("/{userId}")
    public ResponseEntity<SessionsResponse> getSessions(@PathVariable("userId") int userId) {
        System.out.println("in controller");
        return ResponseEntity.ok(
            new SessionsResponse(
                chatService.processSessionsRequest(userId)
            )
        );
    }

    @GetMapping("/{userId}/{sessionId}")
    public ResponseEntity<ChatHistoryResponse> getMessageHistory(
        @PathVariable("userId") int userId,
        @PathVariable("sessionId") int sessionId
    ) {
        System.out.println("in controller");
        return ResponseEntity.ok(
            new ChatHistoryResponse(
                chatService.getChatSessionMessageHistory(userId, sessionId),
                sessionId
            )
        );
    }



    @PostMapping("/{userId}/{sessionId}")
    public ResponseEntity<MessageResponse> receiveMessage(
        @PathVariable("userId") int userId,
        @PathVariable("sessionId") int sessionId,
        @RequestBody MessageInput messageInput) {
        System.out.println("in controller");
        ChatMessage responsesMessage = chatService.processUserMessage(userId, sessionId, messageInput.getMessage());
        return ResponseEntity.ok(
            new MessageResponse(
                responsesMessage.getSentMessage(),
                responsesMessage.getChatSessionId()

            ));
    }
}
