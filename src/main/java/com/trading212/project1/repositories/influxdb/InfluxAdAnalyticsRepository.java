package com.trading212.project1.repositories.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.trading212.project1.repositories.AdAnalyticsRepository;
import com.trading212.project1.repositories.entities.analytical.PropertyType;
import org.springframework.stereotype.Repository;

@Repository
public class InfluxAdAnalyticsRepository implements AdAnalyticsRepository {
    private InfluxDBClient influxDBClient;
    private static final String AD_VIEW_MEASUREMENT = "ad";
    public InfluxAdAnalyticsRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }


    @Override
    public void createAdView(int adId, String country, String subLocally, String neighbourhood, PropertyType propertyType,
                             int price, int views) {
        Point point = new Point(AD_VIEW_MEASUREMENT)
            .addTag("ad_id", String.valueOf(adId))
            .addTag("country", country)
            .addTag("sub_locally", subLocally)
            .addTag("neighbourhood", neighbourhood)
            .addTag("property_type", propertyType == null ? null : propertyType.name())
            .addField("price", price)
            .addField("views", views);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writePoint(point);
    }

}
