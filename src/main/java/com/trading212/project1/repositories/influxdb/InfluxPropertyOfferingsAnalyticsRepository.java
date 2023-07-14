package com.trading212.project1.repositories.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.trading212.project1.repositories.PropertyOfferingsAnalyticsRepository;
import com.trading212.project1.repositories.entities.analytical.PropertyType;
import org.springframework.stereotype.Repository;

@Repository
public class InfluxPropertyOfferingsAnalyticsRepository implements PropertyOfferingsAnalyticsRepository {
    private InfluxDBClient influxDBClient;
    private static final String PROPERTY_MEASUREMENT = "property_offering";

    public InfluxPropertyOfferingsAnalyticsRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @Override
    public void createPropertyOffering(String country, String subLocally, String neighbourhood, int propertyArea,
                                       PropertyType propertyType, int price) {
        Point point = new Point(PROPERTY_MEASUREMENT);
        point.addTag("country", country)
            .addTag("sub_locally", subLocally)
            .addTag("neighbourhood", neighbourhood)
            .addTag("property type", propertyType == null ? null : propertyType.name())
            .addField("price", price)
            .addField("property area", propertyArea);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writePoint(point);
    }
}
