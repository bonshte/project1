package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.repositories.entities.AdFeatureEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdFeatureEntityRowMapper implements RowMapper<AdFeatureEntity> {
    @Override
    public AdFeatureEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AdFeatureEntity(
            rs.getLong("ad_id"),
            rs.getString("feature")
        );
    }
}
