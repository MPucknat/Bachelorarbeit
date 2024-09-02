package de.example.data.finalLocation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeoCoordinates {
    private double latitude;
    private double longitude;
}
