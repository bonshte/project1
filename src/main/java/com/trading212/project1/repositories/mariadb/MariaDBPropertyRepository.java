package com.trading212.project1.repositories.mariadb;

import com.trading212.project1.core.models.PropertyType;
import com.trading212.project1.repositories.PropertyRepository;
import com.trading212.project1.repositories.entities.PropertyEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class MariaDBPropertyRepository implements PropertyRepository {

    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate txTemplate;

    public MariaDBPropertyRepository(JdbcTemplate jdbcTemplate, TransactionTemplate txTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = txTemplate;
    }

    @Override
    public PropertyEntity createProperty(String country, String subLocal, String neighbourhood, double latitude,
                                         double longitude, float propertyArea, PropertyType propertyType, int ownerID,
                                         LocalDate buildDate, int bedroomCount, int bathroomCount,
                                         float propertyRating) {

        return txTemplate.execute( status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement()
            })
        })
    }

    @Override
    public int deletePropertyById(int id) {
        return 0;
    }

    @Override
    public List<PropertyEntity> getProperties() {
        return null;
    }

    @Override
    public Optional<PropertyEntity> getPropertyById(int id) {
        return Optional.empty();
    }

    public static class Queries {
        private static final String CREATE_PROPERTY = """
        INSERT INTO Property(country, sublocally , neighbourhood,
         street, latitude, longitude, property_area, property_type, owner_id, build_date, 
        """
    }
}
