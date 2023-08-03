package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.repositories.entities.ChatMessageEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ChatMessageEntityRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setMessageId(rs.getInt("message_id"));
        chatMessageEntity.setChatSessionId(rs.getInt("chat_session_id"));
        chatMessageEntity.setSentMessage(rs.getString("sent_message"));
        chatMessageEntity.setTranslatedMessage(rs.getString("translated_message"));
        chatMessageEntity.setFromUser(rs.getBoolean("from_user"));
        Timestamp timestamp = rs.getTimestamp("timestamp");
        chatMessageEntity.setTimeSent(timestamp.toLocalDateTime());
        return chatMessageEntity;
    }
}
