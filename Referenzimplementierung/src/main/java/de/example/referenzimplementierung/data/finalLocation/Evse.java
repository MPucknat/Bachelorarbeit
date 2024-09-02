package de.example.referenzimplementierung.data.finalLocation;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Evse {
    private String evseID;

    private List<Connector> connectors;
    private List<String> capabilities;
    private String accessibility;
}
