package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Currency;
import com.trading212.project1.repositories.entities.AdEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdEntityRowMapper implements RowMapper<AdEntity> {
    @Override
    public AdEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdEntity adEntity = new AdEntity();
        adEntity.setId(rs.getLong("id"));
        adEntity.setForSale(rs.getBoolean("for_sale"));
        adEntity.setTown(rs.getString("town"));
        adEntity.setNeighbourhood(rs.getString("neighbourhood"));
        adEntity.setDistrict(rs.getString("district"));
        adEntity.setAccommodationType(AccommodationType.valueOf(rs.getString("accommodation_type")));
        adEntity.setPrice(rs.getInt("price"));
        adEntity.setCurrency(Currency.valueOf(rs.getString("currency")));
        adEntity.setPropertyProvider(rs.getString("property_provider"));
        adEntity.setSize(rs.getInt("size"));
        adEntity.setFloor(rs.getInt("total_floors"));
        adEntity.setGasProvided(rs.getBoolean("gas_provided"));
        adEntity.setThermalPowerPlantProvided(rs.getBoolean("thermal_power_plant_provided"));
        adEntity.setPhoneNumber(rs.getString("phone_number"));
        adEntity.setYearBuilt(rs.getInt("year_built"));
        adEntity.setLink(rs.getString("link"));
        adEntity.setConstruction(rs.getString("construction"));
        adEntity.setDistrict(rs.getString("description"));
        return adEntity;
    }
}
