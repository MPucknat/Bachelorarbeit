package de.example.restdatasender.data.OICP;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GeoCoordinates {
    @JsonProperty("Longitude")
    private double longitude;

    @JsonProperty("Latitude")
    private double latitude;
}
