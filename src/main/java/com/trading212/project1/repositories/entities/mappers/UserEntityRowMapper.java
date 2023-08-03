package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.core.models.Role;
import com.trading212.project1.core.models.User;
import com.trading212.project1.repositories.entities.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserEntityRowMapper implements RowMapper<UserEntity> {
    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(rs.getInt("user_id"));
        userEntity.setEmail(rs.getString("email"));
        userEntity.setPassword(rs.getString("password"));
        String premiumUntilString = rs.getString("premium_until");
        if (premiumUntilString != null) {
            userEntity.setPremiumUntil(LocalDate.parse(premiumUntilString));
        }
        userEntity.setRole(Role.valueOf(rs.getString("role")));
        return userEntity;
    }
}
