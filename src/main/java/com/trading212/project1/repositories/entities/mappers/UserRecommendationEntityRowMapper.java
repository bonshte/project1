package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.repositories.entities.UserRecommendationEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserRecommendationEntityRowMapper implements RowMapper<UserRecommendationEntity> {
    @Override
    public UserRecommendationEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserRecommendationEntity(
                rs.getInt("user_id"),
                rs.getLong("ad_id"),
                rs.getBoolean("for_sale"),
                rs.getTimestamp("recommended_at").toLocalDateTime()
        );
    }
}
