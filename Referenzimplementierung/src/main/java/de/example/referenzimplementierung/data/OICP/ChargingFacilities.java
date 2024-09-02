package de.example.referenzimplementierung.data.OICP;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChargingFacilities {
    @JsonProperty("PowerType")
    private ChargingFacilitiesPowerType PowerType;

    @JsonProperty("Voltage")
    private int Voltage;

    @JsonProperty("Amperage")
    private int Amperage;

    @JsonProperty("Power")
    private int Power;
}
