package com.trading212.project1.api.rest.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
    private String message;
    private boolean fromUser;
    private int chatSessionId;
    private boolean adsFound;
}
