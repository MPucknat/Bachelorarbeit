package de.example.kafkastreams.data.OCPI.Evse.Connector;

import lombok.Data;

@Data
public class Connector {
    private String id;
    private ConnectorStandard standard;
    private ConnectorFormat format;
    private ConnectorPowerType power_type;
    private int max_voltage;
    private int max_amperage;
    private int max_electric_power;
    private String last_updated;
}
