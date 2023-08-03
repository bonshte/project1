package com.trading212.project1.repositories.mariadb;

import com.trading212.project1.core.models.Role;
import com.trading212.project1.repositories.entities.UserEntity;
import com.trading212.project1.repositories.UserRepository;
import com.trading212.project1.repositories.entities.mappers.UserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class MariaDBUserRepository implements UserRepository {
    private TransactionTemplate txTemplate;
    private JdbcTemplate jdbcTemplate;

    public MariaDBUserRepository(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = transactionTemplate;
    }

    @Override
    public Optional<UserEntity> getUser(String email) {
        return jdbcTemplate.query(Queries.SELECT_BY_EMAIL, new UserEntityRowMapper(), email)
                .stream()
                .findFirst();
    }

    @Override
    public List<UserEntity> getUsers() {
        return jdbcTemplate.query(Queries.SELECT_ALL_USERS, new UserEntityRowMapper());
    }

    @Override
    public UserEntity createUser(String email, Role role,
                                 String password) {
        return txTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(Queries.CREATE_USER, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, email);
                ps.setString(2, password);
                ps.setString(3, role.roleName);

                return ps;
            }, keyHolder);
            Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();

            UserEntity createdUser = new UserEntity();
            createdUser.setId(id);
            createdUser.setEmail(email);
            createdUser.setPassword(password);
            createdUser.setRole(role);
            return createdUser;
        });
    }


    @Override
    public void setPremiumUser(int userId, LocalDate until) {
        jdbcTemplate.update(Queries.UPDATE_USER_PREMIUM, until, userId);
    }

    @Override
    public int deleteUser(int userId) {
        return jdbcTemplate.update(Queries.DELETE_BY_ID, userId);
    }

    static class Queries {
        private static final String UPDATE_USER_PREMIUM = """
            UPDATE user
            SET premium_until = ?
            WHERE user_id = ?
            """;

        private static final String SELECT_BY_EMAIL = """
            SELECT user_id, email, password, premium_until, role
            FROM user
            WHERE email = ?;
            """;

        private static final String SELECT_ALL_USERS = """
                SELECT user_id, email, password, premium_until,  role
                FROM user;
                """;

        private static final String DELETE_BY_ID = """
                DELETE FROM user
                WHERE user_id = ?;
                """;
        private static final String CREATE_USER = """
                INSERT INTO user(email,password, role)
                VALUES(?,?,?);
                """;
    }
}
