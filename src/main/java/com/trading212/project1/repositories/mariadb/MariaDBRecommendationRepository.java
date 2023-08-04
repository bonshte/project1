package com.trading212.project1.repositories.mariadb;

import com.trading212.project1.repositories.RecommendationRepository;
import com.trading212.project1.repositories.entities.ChatSessionRecommendationEntity;
import com.trading212.project1.repositories.entities.mappers.ChatSessionRecommendationEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MariaDBRecommendationRepository implements RecommendationRepository {
    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate txTemplate;

    public MariaDBRecommendationRepository(JdbcTemplate jdbcTemplate, TransactionTemplate txTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = txTemplate;
    }

    @Override
    public List<ChatSessionRecommendationEntity> getRecommendationsForSession(int sessionId) {
        return jdbcTemplate.query(Queries.GET_RECOMMENDATIONS_FOR_SESSION, new ChatSessionRecommendationEntityRowMapper() , sessionId);
    }

    @Override
    public List<ChatSessionRecommendationEntity> getRecommendationsForUser(int userId) {
        return jdbcTemplate.query(Queries.GET_RECOMMENDATIONS_FOR_USER,
                new ChatSessionRecommendationEntityRowMapper(), userId);
    }

    @Override
    public ChatSessionRecommendationEntity createRecommendationForSession(Long adId, int sessionId,
                                                                          LocalDateTime timestamp, int userId, boolean forSale) {
        jdbcTemplate.update(Queries.CREATE_RECOMMENDATION_FOR_SESSION, sessionId,
            adId, userId, timestamp, forSale);
        return new ChatSessionRecommendationEntity(
            sessionId,
            adId,
            userId,
            timestamp,
            forSale
        );
    }



    static class Queries {

        private static final String CREATE_RECOMMENDATION_FOR_SESSION = """
                INSERT INTO chatSessionRecommendation (chat_session_id, ad_id, user_id, recommended_at, for_sale)
                 VALUES (?, ?, ?, ?, ?);
                """;
        private static final String GET_RECOMMENDATIONS_FOR_SESSION = """
                SELECT chat_session_id, ad_id, user_id, recommended_at, for_sale
                FROM chatSessionRecommendation
                WHERE chat_session_id = ?;
                """;

        private static final String GET_RECOMMENDATIONS_FOR_USER = """
                SELECT chat_session_id, ad_id, user_id, recommended_at, for_sale
                FROM userRecommendation
                WHERE user_id = ?;
                """;
    }
}
