package com.trading212.project1.repositories.entities.mappers;

import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Currency;
import com.trading212.project1.repositories.entities.AdEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class AdEntityRowMapper implements RowMapper<AdEntity> {
    @Override
    public AdEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdEntity ad = new AdEntity();

        ad.setAdId(rs.getLong("ad_id"));
        ad.setTown(rs.getString("town"));
        ad.setNeighbourhood(rs.getString("neighbourhood"));
        ad.setDistrict(rs.getString("district"));

        ad.setAccommodationType(AccommodationType.valueOf(rs.getString("accommodation_type").toUpperCase()));

        ad.setPrice(rs.getInt("price"));

        ad.setCurrency(Currency.valueOf(rs.getString("currency").toUpperCase()));

        ad.setPropertyProvider(rs.getString("property_provider"));
        ad.setSize(rs.getInt("size"));
        ad.setFloor(rs.getInt("floor"));
        ad.setTotalFloors(rs.getInt("total_floors"));
        ad.setGasProvided(rs.getBoolean("gas_provided"));
        ad.setThermalPowerPlantProvided(rs.getBoolean("thermal_power_plant_provided"));
        ad.setPhoneNumber(rs.getString("phone_number"));
        ad.setYearBuilt(rs.getInt("year_built"));
        ad.setLink(rs.getString("link"));
        ad.setConstruction(rs.getString("construction"));
        ad.setDescription(rs.getString("description"));
        ad.setForSale(rs.getBoolean("for_sale"));

        String features = rs.getString("features");
        if (features != null) {
            ad.setFeatures(Arrays.asList(features.split(",")));
        } else {
            ad.setFeatures(new ArrayList<>());
        }

        String imageUrls = rs.getString("imageUrls");
        if (imageUrls != null) {
            ad.setImageUrls(Arrays.asList(imageUrls.split(",")));
        } else {
            ad.setImageUrls(new ArrayList<>());
        }

        return ad;
    }
}
