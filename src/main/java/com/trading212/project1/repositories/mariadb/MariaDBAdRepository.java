package com.trading212.project1.repositories.mariadb;

import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Currency;
import com.trading212.project1.repositories.AdRepository;
import com.trading212.project1.repositories.entities.AdEntity;
import com.trading212.project1.repositories.entities.mappers.AdEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

@Repository
public class MariaDBAdRepository implements AdRepository {

    public final JdbcTemplate jdbcTemplate;
    public final TransactionTemplate txTemplate;

    public MariaDBAdRepository(JdbcTemplate jdbcTemplate, TransactionTemplate txTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = txTemplate;
    }

    @Override
    public AdEntity createAd(String town, String neighbourhood, String district, AccommodationType accommodationType, Integer price,
                       Currency currency, String propertyProvider, Integer size, Integer floor, Integer totalFloors,
                       Boolean gasProvided, Boolean thermalPowerPlantProvided, String phoneNumber, Integer yearBuilt,
                       String link, String construction, String description, Boolean forSale,
                       List<String> features, List<String> imageUrls) {

        return txTemplate.execute(transactionStatus -> {


            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(Queries.CREATE_AD, Statement.RETURN_GENERATED_KEYS);

                setStringOrNull(ps, 1, town);
                setStringOrNull(ps, 2, neighbourhood);
                setStringOrNull(ps, 3, district);
                setStringOrNull(ps, 4, accommodationType != null ? accommodationType.toString() : null);
                setIntegerOrNull(ps, 5, price);
                setStringOrNull(ps, 6, currency != null ? currency.toString() : null);
                setStringOrNull(ps, 7, propertyProvider);
                setIntegerOrNull(ps, 8, size);
                setIntegerOrNull(ps, 9, floor);
                setIntegerOrNull(ps, 10, totalFloors);
                setBooleanOrNull(ps, 11, gasProvided);
                setBooleanOrNull(ps, 12, thermalPowerPlantProvided);
                setStringOrNull(ps, 13, phoneNumber);
                setIntegerOrNull(ps, 14, yearBuilt);
                setStringOrNull(ps, 15, link);
                setStringOrNull(ps, 16, construction);
                setStringOrNull(ps, 17, description);
                setBooleanOrNull(ps, 18, forSale);

                return ps;
            }, keyHolder);

            Long adId = keyHolder.getKey().longValue();



            for (String feature : features) {

                jdbcTemplate.update(Queries.CREATE_FEATURE_FOR_AD, adId, feature);
            }

            for (String imageUrl : imageUrls) {
                jdbcTemplate.update(Queries.CREATE_IMAGE_FOR_AD, adId, imageUrl);
            }

            AdEntity adEntity = new AdEntity();
            adEntity.setAdId(adId);
            adEntity.setTown(town);
            adEntity.setNeighbourhood(neighbourhood);
            adEntity.setDistrict(district);
            adEntity.setAccommodationType(accommodationType);
            adEntity.setPrice(price);
            adEntity.setCurrency(currency);
            adEntity.setPropertyProvider(propertyProvider);
            adEntity.setSize(size);
            adEntity.setFloor(floor);
            adEntity.setTotalFloors(totalFloors);
            adEntity.setGasProvided(gasProvided);
            adEntity.setThermalPowerPlantProvided(thermalPowerPlantProvided);
            adEntity.setPhoneNumber(phoneNumber);
            adEntity.setYearBuilt(yearBuilt);
            adEntity.setLink(link);
            adEntity.setConstruction(construction);
            adEntity.setDescription(description);
            adEntity.setForSale(forSale);
            adEntity.setFeatures(features);
            adEntity.setImageUrls(imageUrls);

            return adEntity;
        });
    }

    public AdEntity getByAdId(Long adId) {
        System.out.println(adId);
        return jdbcTemplate.queryForObject(Queries.GET_AD_BY_ID,  new AdEntityRowMapper(), adId);
    }


    @Override
    public void deleteAdById(Long adId) {
        jdbcTemplate.update(Queries.DELETE_AD_BY_ID, adId);
    }

    @Override
    public List<AdEntity> getAllAdsByOffer(boolean forSale) {
        return jdbcTemplate.query(Queries.GET_ALL_ADS_BY_OFFER, new Object[]{forSale}, new AdEntityRowMapper());
    }

    private void setStringOrNull(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
        if (value == null) {
            ps.setNull(parameterIndex, Types.VARCHAR);
        } else {
            ps.setString(parameterIndex, value);
        }
    }

    private void setIntegerOrNull(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(parameterIndex, Types.INTEGER);
        } else {
            ps.setInt(parameterIndex, value);
        }
    }

    private void setBooleanOrNull(PreparedStatement ps, int parameterIndex, Boolean value) throws SQLException {
        if (value == null) {
            ps.setNull(parameterIndex, Types.BOOLEAN);
        } else {
            ps.setBoolean(parameterIndex, value);
        }
    }



    @Override
    public List<AdEntity> getAllAds() {
        return jdbcTemplate.query(Queries.GET_ALL_ADS, new AdEntityRowMapper());
    }


    static class Queries {
        private static final String GET_ALL_ADS_BY_OFFER = "SELECT ads.ad_id, ads.for_sale, ads.town, ads.neighbourhood, ads.district, ads.accommodation_type, ads.price, " +
            "ads.currency, ads.property_provider, ads.size, ads.floor, ads.total_floors, ads.gas_provided, " +
            "ads.thermal_power_plant_provided, ads.phone_number, ads.year_built, ads.link, ads.construction, " +
            "ads.description, GROUP_CONCAT(DISTINCT adFeature.feature) as features, " +
            "GROUP_CONCAT(DISTINCT adImageUrl.image_url) as imageUrls " +
            "FROM ads " +
            "LEFT JOIN adFeature ON ads.ad_id = adFeature.ad_id " +
            "LEFT JOIN adImageUrl ON ads.ad_id = adImageUrl.ad_id " +
            "WHERE ads.for_sale = ? " +
            "GROUP BY ads.ad_id, ads.for_sale, ads.town, ads.neighbourhood, ads.district, ads.accommodation_type, ads.price, " +
            "ads.currency, ads.property_provider, ads.size, ads.floor, ads.total_floors, ads.gas_provided, " +
            "ads.thermal_power_plant_provided, ads.phone_number, ads.year_built, ads.link, ads.construction, ads.description";

        private static final String GET_ALL_ADS = "SELECT ads.ad_id, ads.for_sale, ads.town, ads.neighbourhood, ads.district, ads.accommodation_type, ads.price, " +
            "ads.currency, ads.property_provider, ads.size, ads.floor, ads.total_floors, ads.gas_provided, " +
            "ads.thermal_power_plant_provided, ads.phone_number, ads.year_built, ads.link, ads.construction, " +
            "ads.description, GROUP_CONCAT(DISTINCT adFeature.feature) as features, " +
            "GROUP_CONCAT(DISTINCT adImageUrl.image_url) as imageUrls " +
            "FROM ads " +
            "LEFT JOIN adFeature ON ads.ad_id = adFeature.ad_id " +
            "LEFT JOIN adImageUrl ON ads.ad_id = adImageUrl.ad_id " +
            "GROUP BY ads.ad_id, ads.for_sale, ads.town, ads.neighbourhood, ads.district, ads.accommodation_type, ads.price, " +
            "ads.currency, ads.property_provider, ads.size, ads.floor, ads.total_floors, ads.gas_provided, " +
            "ads.thermal_power_plant_provided, ads.phone_number, ads.year_built, ads.link, ads.construction, ads.description";
        private static final String GET_AD_BY_ID =  "SELECT ads.ad_id, ads.for_sale, ads.town, ads.neighbourhood, ads.district, ads.accommodation_type, ads.price, " +
            "ads.currency, ads.property_provider, ads.size, ads.floor, ads.total_floors, ads.gas_provided, " +
            "ads.thermal_power_plant_provided, ads.phone_number, ads.year_built, ads.link, ads.construction, " +
            "ads.description, GROUP_CONCAT(DISTINCT adFeature.feature) as features, " +
            "GROUP_CONCAT(DISTINCT adImageUrl.image_url) as imageUrls " +
            "FROM ads " +
            "LEFT JOIN adFeature ON ads.ad_id = adFeature.ad_id " +
            "LEFT JOIN adImageUrl ON ads.ad_id = adImageUrl.ad_id " +
            "WHERE ads.ad_id = ? " +
            "GROUP BY ads.ad_id, ads.for_sale, ads.town, ads.neighbourhood, ads.district, ads.accommodation_type, ads.price, " +
            "ads.currency, ads.property_provider, ads.size, ads.floor, ads.total_floors, ads.gas_provided, " +
            "ads.thermal_power_plant_provided, ads.phone_number, ads.year_built, ads.link, ads.construction, ads.description";

        private static final String CREATE_AD = "INSERT INTO ads (town, neighbourhood, district, accommodation_type, price, " +
            "currency, property_provider, size, floor, total_floors, gas_provided, thermal_power_plant_provided, " +
            "phone_number, year_built, link, construction, description, for_sale) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        private static final String DELETE_AD_BY_ID = "DELETE FROM ads WHERE ad_id = ?";

        private static final String CREATE_IMAGE_FOR_AD = "INSERT INTO adImageUrl (ad_id, image_url) VALUES (?, ?)";
        private static final String CREATE_FEATURE_FOR_AD = "INSERT INTO adFeature (ad_id, feature) VALUES (?, ?)";

        private static final String DELETE_ALL_AD_RECORDS = "DELETE FROM ads";
    }
}
