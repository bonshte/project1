package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.repositories.entities.AdImageEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdImageEntityRowMapper implements RowMapper<AdImageEntity> {
    @Override
    public AdImageEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AdImageEntity(
                rs.getLong("ad_id"),
                rs.getString("image_url")
        );
    }
}
