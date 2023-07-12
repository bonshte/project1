package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.core.models.Role;
import com.trading212.project1.repositories.entities.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserEntityRowMapper implements RowMapper<UserEntity> {
    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserEntity(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getString("password"),
                LocalDate.parse(rs.getString("created_at")),
                rs.getFloat("user_rating"),
                Role.valueOf(rs.getString("role"))
        );
    }
}
