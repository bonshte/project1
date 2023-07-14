package com.trading212.project1.repositories.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.trading212.project1.repositories.UserRequirementsAnalyticsRepository;
import com.trading212.project1.repositories.entities.analytical.PropertyType;
import org.springframework.stereotype.Repository;

@Repository
public class InfluxUserRequirementsAnalysisRepository implements UserRequirementsAnalyticsRepository {
    private static final String USER_REQUIREMENTS_MEASUREMENT = "user_requirements";
    private InfluxDBClient influxDBClient;
    public InfluxUserRequirementsAnalysisRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @Override
    public void createUserRequirement(String country, String subLocally, String neighbourhood,
                                      PropertyType propertyType, int price) {
        Point point = new Point(USER_REQUIREMENTS_MEASUREMENT);
        point.addTag("country", country)
            .addTag("sub_locally", subLocally)
            .addTag("neighbourhood", neighbourhood)
            .addTag("property_type", propertyType == null ? null : propertyType.name())
            .addField("price", price);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writePoint(point);
    }

}
