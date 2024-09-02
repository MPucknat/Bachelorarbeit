package de.example.referenzimplementierung.data.finalLocation;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LocationFinal {

    private String operatorID;
    private String operatorName;
    private String poolID;

    private Address address;
    private GeoCoordinates coordinates;
    private List<Evse> evses;

    private boolean open24Hours;

    private String lastUpdate;
}
