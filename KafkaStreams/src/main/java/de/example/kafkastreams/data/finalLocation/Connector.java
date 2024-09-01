package de.example.kafkastreams.data.finalLocation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Connector {
    private String powerType;
    private int voltage;
    private int amperage;
    private int power;
}
