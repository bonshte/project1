package com.trading212.project1.api.rest.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionsResponse {
    private List<ChatSessionResponse> chatSessions;
}
