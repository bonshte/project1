package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.repositories.entities.ChatSessionRecommendationEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatSessionRecommendationEntityRowMapper implements RowMapper<ChatSessionRecommendationEntity> {
    @Override
    public ChatSessionRecommendationEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ChatSessionRecommendationEntity(
                rs.getInt("chat_session_id"),
                rs.getLong("ad_id"),
                rs.getInt("user_id"),
                rs.getTimestamp("recommended_at").toLocalDateTime(),
                rs.getBoolean("for_sale")
        );
    }
}
