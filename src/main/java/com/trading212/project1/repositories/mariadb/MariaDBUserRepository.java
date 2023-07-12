package com.trading212.project1.repositories.mariadb;

import com.trading212.project1.core.models.Role;
import com.trading212.project1.repositories.entities.UserEntity;
import com.trading212.project1.repositories.UserRepository;
import com.trading212.project1.repositories.entities.mappers.ClientEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import java.sql.Date;
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
    public Optional<UserEntity> getClientByUsername(String username) {
        return jdbcTemplate.query(Queries.SELECT_BY_USERNAME, new ClientEntityRowMapper(), username)
                .stream()
                .findFirst();
    }

    @Override
    public List<UserEntity> getClients() {
        return jdbcTemplate.query(Queries.SELECT_ALL_CLIENTS, new ClientEntityRowMapper());
    }

    @Override
    public UserEntity createClient(String username, String email, String phoneNumber, Role role,
                                   String password, LocalDate dateCreated, float rating) {
        return txTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(Queries.CREATE_CLIENT, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, phoneNumber);
                ps.setString(4, password);
                ps.setString(5, role.roleName);
                ps.setDate(6, Date.valueOf(dateCreated));
                ps.setFloat(7,rating);
                return ps;
            }, keyHolder);
            Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();

            return new UserEntity(id,username,email,phoneNumber,password, dateCreated, rating, role);
        });
    }

    @Override
    public int deleteClientByUsername(String username) {
            return jdbcTemplate.update(Queries.DELETE_BY_USERNAME, username);
    }

    static class Queries {
        private static final String SELECT_BY_USERNAME = """
            SELECT *
            FROM User
            WHERE username = ?;
            """;

        private static final String SELECT_ALL_CLIENTS = """
                SELECT *
                FROM User;
                """;

        private static final String DELETE_BY_USERNAME = """
                DELETE FROM User
                WHERE username = ?;
                """;
        private static final String CREATE_CLIENT = """
                INSERT INTO User(username,email,phone_number,password, role, created_at, user_rating)
                VALUES(?,?,?,?,?,?,?);
                """;
    }
}
