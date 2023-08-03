package com.trading212.project1.repositories.mariadb;

import com.trading212.project1.repositories.RecommendationRepository;
import com.trading212.project1.repositories.entities.ChatSessionRecommendationEntity;
import com.trading212.project1.repositories.entities.UserRecommendationEntity;
import com.trading212.project1.repositories.entities.mappers.UserRecommendationEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

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
        return jdbcTemplate.query(Queries.GET_RECOMMENDATIONS_FOR_SESSION, ,sessionId);
    }

    @Override
    public List<UserRecommendationEntity> getRecommendationsForUser(int userId) {
        return jdbcTemplate.query(Queries.GET_RECOMMENDATIONS_FOR_USER, new UserRecommendationEntityRowMapper(), userId);
    }

    static class Queries {
        private static final String GET_RECOMMENDATIONS_FOR_SESSION = """
                SELECT chat_session_id, ad_id, recommended_at
                FROM chatSessionRecommendation
                WHERE chat_session_id = ?;
                """;

        private static final String GET_RECOMMENDATIONS_FOR_USER = """
                SELECT user_id, ad_id, for_sale, recommended_at
                FROM userRecommendation
                WHERE user_id = ?;
                """;
    }
}
