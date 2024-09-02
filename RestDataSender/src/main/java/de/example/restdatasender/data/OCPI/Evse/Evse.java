package de.example.restdatasender.data.OCPI.Evse;

import de.example.restdatasender.data.OCPI.Evse.Connector.Connector;
import de.example.restdatasender.data.OCPI.Status;
import lombok.Data;

import java.util.List;

@Data
public class Evse {
    private String uid;
    private String evse_id;
    private Status status;
    private List<Capabilities> capabilities;
    private List<Connector> connectors;
    private ParkingRestriction parking_restrictions;
    private String last_updated;
}
