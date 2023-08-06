package com.trading212.project1.core;

import com.trading212.project1.core.models.ChatSession;
import com.trading212.project1.core.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    private final ChatSessionService chatSessionService;

    public AuthorizationService(ChatSessionService chatSessionService) {
        this.chatSessionService = chatSessionService;
    }

    public boolean isAuthorizedUserDataRequest(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userId == user.getId();
    }

    public boolean isAuthorizedUserSessionRequest(int userId, int sessionId) {
        ChatSession chatSession = chatSessionService.getChatSession(sessionId);
        return isAuthorizedUserDataRequest(userId) && userId == chatSession.getUserId();
    }
}
