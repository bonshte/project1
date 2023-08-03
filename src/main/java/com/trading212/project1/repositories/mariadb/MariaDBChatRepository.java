package com.trading212.project1.repositories.mariadb;

import com.trading212.project1.repositories.ChatRepository;
import com.trading212.project1.repositories.entities.ChatMessageEntity;
import com.trading212.project1.repositories.entities.ChatSessionEntity;
import com.trading212.project1.repositories.entities.mappers.ChatMessageEntityRowMapper;
import com.trading212.project1.repositories.entities.mappers.ChatSessionEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class MariaDBChatRepository implements ChatRepository {
    private TransactionTemplate txTemplate;
    private JdbcTemplate jdbcTemplate;

    public MariaDBChatRepository(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = transactionTemplate;
    }

    @Override
    public ChatSessionEntity createChatSession(int userId, String description) {
        return txTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(Queries.CREATE_CHAT_SESSION,
                    Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, userId);
                ps.setString(2, description);

                return ps;
            }, keyHolder);
            Integer sessionId = Objects.requireNonNull(keyHolder.getKey()).intValue();
            ChatSessionEntity newChatSession = ChatSessionEntity.builder()
                .userId(userId)
                .sessionId(sessionId)
                .description(description)
                .build();
            return newChatSession;
        });
    }

    @Override
    public List<ChatSessionEntity> getChatSessionsForUser(int userId) {
        return jdbcTemplate.query(Queries.GET_CHAT_SESSIONS_FOR_USER, new ChatSessionEntityRowMapper(), userId);
    }

    @Override
    public ChatMessageEntity createMessage(int chatSessionId, String sentMessage, String translatedMessage,
                                           boolean fromUser, LocalDateTime timestamp) {
        return txTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(Queries.CREATE_CHAT_MESSAGE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, chatSessionId);
                ps.setString(2, sentMessage);
                ps.setString(3, translatedMessage);
                ps.setBoolean(4, fromUser);
                ps.setTimestamp(5, Timestamp.valueOf(timestamp));

                return ps;
            }, keyHolder);
            Integer messageId = Objects.requireNonNull(keyHolder.getKey()).intValue();

            ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
            chatMessageEntity.setChatSessionId(chatSessionId);
            chatMessageEntity.setSentMessage(sentMessage);
            chatMessageEntity.setTranslatedMessage(translatedMessage);
            chatMessageEntity.setFromUser(fromUser);
            chatMessageEntity.setTimeSent(timestamp);
            chatMessageEntity.setMessageId(messageId);
            return chatMessageEntity;
        });
    }

    @Override
    public List<ChatMessageEntity> getChatSessionMessages(int sessionId) {
        return jdbcTemplate.query(Queries.GET_CHAT_SESSION_MESSAGES, new ChatMessageEntityRowMapper(), sessionId);
    }

    @Override
    public Optional<ChatSessionEntity> getChatSession(int sessionId) {
        return jdbcTemplate.query(Queries.GET_CHAT_SESSION, new ChatSessionEntityRowMapper(), sessionId)
            .stream()
            .findFirst();
    }

    static class Queries {
        private static final String CREATE_CHAT_SESSION = """
            INSERT INTO chatSession(user_id, description)
            VALUES(?, ?);
            """;

        private static final String GET_CHAT_SESSIONS_FOR_USER = """
            SELECT chat_session_id, description, user_id
            FROM chatSession
            WHERE user_id = ?;
            """;

        private static final String CREATE_CHAT_MESSAGE = """
            INSERT INTO chatMessage(chat_session_id, sent_message, translated_message, from_user, timestamp)
            VALUES(?,?,?,?,?);
            """;

        private static final String GET_CHAT_SESSION_MESSAGES = """
            SELECT message_id, chat_session_id,sent_message, translated_message, from_user, timestamp
            FROM chatMessage
            WHERE chat_session_id = ?;
            """;

        private static final String GET_CHAT_SESSION = """
            SELECT chat_session_id, description, user_id
            FROM chatSession
            WHERE chat_session_id = ?;
            """;
    }
}
