package com.trading212.project1.repositories.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.trading212.project1.repositories.entities.analytical.PropertyType;

import java.time.Instant;
import java.util.Random;


public class InfluxDB2Example {
    public static void main(String[] args) {
        // You can generate an API token from the "API Tokens Tab" in the UI
        String token = "FoqQS9O-03yn88wGXYolENm-L2OfzOSXDUBwjWS8fg3CPhE6Lo0hfVpFBVD4mUEoDlGF4nC6IPfyLAc4gTxcrw==";
        String bucket = "accommodation1";
        String org = "Trading212";

        InfluxDBClient client = InfluxDBClientFactory.create("http://localhost:8086", token.toCharArray());
        String data = "adsTest,host=GAY used_percent=1";
        WriteApiBlocking writeApi = client.getWriteApiBlocking();



        Random rand = new Random();

        for (int i = 0; i < 100; ++i) {
            int price = rand.nextInt(1000);
            price += 20;
            int area = 20 * rand.nextInt(1,20);

            int type = rand.nextInt(PropertyType.values().length);
            PropertyType propertyType = PropertyType.values()[type];
            var point = writePropertyData("Bulgaria", "Sofia", null, propertyType.name(), price, area);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
            writeApi.writePoint(bucket, org, point);
        }





//        WriteApiBlocking writeApi = client.getWriteApiBlocking();
//        writeApi.writeRecord(bucket, org, point);
    }

    public static Point writePropertyData(String country, String locality, String neighborhood, String propertyType,
                                          double price, int area) {
//        private String country;
//        private String locally;
//        private String neighbourhood;
//        private int price;
//        private String areaRange;
//        private PropertyType propertyType;
        Point point = Point.measurement("property_test")
            .addTag("country", country)
            .addTag("locality", locality)
            .addTag("neighborhood", neighborhood)
            .addTag("property type", propertyType)
            .addTag("property area", String.valueOf(area))
            .addField("price", price)
            .time(Instant.now(), WritePrecision.NS);
        return point;

    }
}

/*

 */