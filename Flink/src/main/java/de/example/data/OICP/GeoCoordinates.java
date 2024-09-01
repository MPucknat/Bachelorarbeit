package de.example.data.OICP;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class GeoCoordinates {
    @JsonProperty("Longitude")
    private double longitude;

    @JsonProperty("Latitude")
    private double latitude;
}
