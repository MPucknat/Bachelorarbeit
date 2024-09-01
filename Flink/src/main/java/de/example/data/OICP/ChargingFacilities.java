package de.example.data.OICP;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

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
