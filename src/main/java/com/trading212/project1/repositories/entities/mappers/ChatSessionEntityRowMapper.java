package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.repositories.entities.ChatSessionEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatSessionEntityRowMapper implements RowMapper<ChatSessionEntity> {
    @Override
    public ChatSessionEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChatSessionEntity chatSessionEntity = new ChatSessionEntity();
        chatSessionEntity.setSessionId(rs.getInt("chat_session_id"));
        chatSessionEntity.setDescription((rs.getString("description")));
        chatSessionEntity.setUserId(rs.getInt("user_id"));
        return chatSessionEntity;
    }
}
