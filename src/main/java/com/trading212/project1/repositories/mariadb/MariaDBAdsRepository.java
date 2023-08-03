package com.trading212.project1.repositories.mariadb;

import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Currency;
import com.trading212.project1.repositories.AdsRepository;
import com.trading212.project1.repositories.entities.AdEntity;
import com.trading212.project1.repositories.entities.AdFeatureEntity;
import com.trading212.project1.repositories.entities.AdImageEntity;
import com.trading212.project1.repositories.entities.mappers.AdEntityRowMapper;
import com.trading212.project1.repositories.entities.mappers.AdFeatureEntityRowMapper;
import com.trading212.project1.repositories.entities.mappers.AdImageEntityRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
@Repository
public class MariaDBAdsRepository implements AdsRepository {
    private TransactionTemplate txTemplate;
    private JdbcTemplate jdbcTemplate;

    public MariaDBAdsRepository(TransactionTemplate txTemplate, JdbcTemplate jdbcTemplate) {
        this.txTemplate = txTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AdEntity createAd(AdEntity adEntity) {
        return txTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(Queries.CREATE_AD_META_DATA, Statement.RETURN_GENERATED_KEYS);
                ps.setBoolean(1, adEntity.getForSale());
                ps.setString(2, adEntity.getTown());
                ps.setString(3, adEntity.getNeighbourhood());
                ps.setString(4, adEntity.getDistrict());
                ps.setString(5, adEntity.getAccommodationType().toString());
                ps.setInt(6, adEntity.getPrice());
                ps.setString(7, adEntity.getCurrency().toString());
                ps.setString(8, adEntity.getPropertyProvider());
                ps.setInt(9, adEntity.getSize());
                ps.setInt(10, adEntity.getFloor());
                ps.setInt(11, adEntity.getTotalFloors());
                ps.setBoolean(12, adEntity.getGasProvided());
                ps.setBoolean(13, adEntity.getThermalPowerPlantProvided());
                ps.setString(14, adEntity.getPhoneNumber());
                ps.setInt(15, adEntity.getYearBuilt());
                ps.setString(16, adEntity.getLink());
                ps.setString(17, adEntity.getConstruction());
                ps.setString(18, adEntity.getDescription());

                return ps;
            }, keyHolder);
            Long adId = Objects.requireNonNull(keyHolder.getKey()).longValue();



            return new AdEntity(
                adId,
                    adEntity.getTown(),
                    adEntity.getNeighbourhood(),
                    adEntity.getDistrict(),
                    adEntity.getAccommodationType(),
                    adEntity.getPrice(),
                    adEntity.getCurrency(),
                    adEntity.getPropertyProvider(),
                    adEntity.getSize(),
                    adEntity.getFloor(),
                    adEntity.getTotalFloors(),
                    adEntity.getGasProvided(),
                    adEntity.getThermalPowerPlantProvided(),
                    adEntity.getPhoneNumber(),
                    adEntity.getYearBuilt(),
                    adEntity.getLink(),
                    adEntity.getConstruction(),
                    adEntity.getDescription(),
                    adEntity.getForSale()
            );
        });
    }

    @Override
    public List<AdEntity> getAdsByLinks(List<String> links) {
        String inSql = String.join(",", Collections.nCopies(links.size(), "?"));
        String sql = "SELECT ad_id,for_sale,town, neighbourhood, district, accommodation_type," +
                "                price, currency, property_provider, size, floor, total_floors, gas_provided, thermal_power_plant_provided," +
                "                phone_number,year_built,link,construction,description FROM ads WHERE link IN (" + inSql + ")";

        return jdbcTemplate.query(sql, links.toArray(), (rs, rowNum) -> {
            AdEntity ad = new AdEntity();
            // Assuming you have the corresponding setter methods in AdEntity
            ad.setId(rs.getLong("ad_id"));
            ad.setForSale(rs.getBoolean("for_sale"));
            ad.setTown(rs.getString("town"));
            ad.setNeighbourhood(rs.getString("neighbourhood"));
            ad.setDistrict(rs.getString("district"));
            ad.setAccommodationType(AccommodationType.valueOf(rs.getString("accommodation_type")));
            ad.setPrice(rs.getInt("price"));
            ad.setCurrency(Currency.valueOf(rs.getString("currency")));
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
            return ad;
        });
    }

    @Override
    public int deleteAdsNotInLinks(List<String> links) {
        String inSql = String.join(",", Collections.nCopies(links.size(), "?"));
        String sql = "DELETE FROM ads WHERE link NOT IN (" + inSql + ")";

        return jdbcTemplate.update(sql, links.toArray());
    }

    @Override
    public int createFeaturesForAd(int adId, List<String> features) {
        return txTemplate.execute(status -> {
            int[] updateCounts = jdbcTemplate.batchUpdate(Queries.CREATE_FEATURE_FOR_AD, new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, adId);
                    ps.setString(2, features.get(i));
                }
                public int getBatchSize() {
                    return features.size();
                }
            });
            return Arrays.stream(updateCounts).sum();
        });
    }

    @Override
    public List<AdImageEntity> getImagesForAd(int adId) {
        return jdbcTemplate.query(Queries.GET_IMAGES_FOR_AD, new AdImageEntityRowMapper(), adId);
    }

    @Override
    public List<AdFeatureEntity> getFeaturesForAd(int adId) {
        return jdbcTemplate.query(Queries.GET_FEATURES_FOR_AD, new AdFeatureEntityRowMapper(), adId);
    }

    @Override
    public int createImagesForAd(int adId, List<String> images) {
        int [] updateCounts = jdbcTemplate.batchUpdate(Queries.CREATE_IMAGE_FOR_AD, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, adId);
                ps.setString(2, images.get(i));
            }

            @Override
            public int getBatchSize() {
                return images.size();
            }
        });
        return Arrays.stream(updateCounts).sum();
    }

    @Override
    public int deleteAd(int adId) {
        return jdbcTemplate.update(Queries.DELETE_AD_BY_ID, adId);
    }

    @Override
    public Optional<AdEntity> getAdById(int adId) {
        return jdbcTemplate.query(Queries.GET_AD_BY_ID, new AdEntityRowMapper(), adId)
                .stream()
                .findFirst();
    }

    static class Queries {


        private static final String CREATE_AD_META_DATA = """
                INSERT INTO ads(for_sale,town, neighbourhood, district, accommodation_type,
                price, currency, property_provider, size, floor, total_floors, gas_provided, thermal_power_plant_provided,
                phone_number,year_built,link,construction,description)
                VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);
                """;

        private static final String GET_AD_BY_ID = """
                SELECT for_sale,town, neighbourhood, district, accommodation_type,
                price, currency, property_provider, size, floor, total_floors, gas_provided, thermal_power_plant_provided,
                phone_number,year_built,link,construction,description
                FROM ads
                WHERE id = ?;
                """;

        private static final String DELETE_AD_BY_ID = """
                DELETE FROM ads
                WHERE id = ?;
                """;

        private static final String CREATE_IMAGE_FOR_AD = """
                INSERT INTO adImageUrl(ad_id, imageUrl)
                VALUES (?,?);
                """;
        private static final String CREATE_FEATURE_FOR_AD = """
                INSERT INTO adFeature(ad_id,feature)
                VALUES(?,?);
                """;

        private static final String GET_IMAGES_FOR_AD = """
                SELECT imageUrl
                FROM adImageUrl
                WHERE ad_id = ?;
                """;

        private static final String GET_FEATURES_FOR_AD = """
                SELECT feature
                FROM adFeature
                WHERE ad_id = ?;
                """;
    }
}
