package de.example.kafkastreams.data.OCPI;

import de.example.kafkastreams.data.OCPI.Evse.Evse;
import lombok.Data;

import java.util.List;

@Data
public class LocationOCPI {

    private String country_code;
    private String party_id;
    private String id;
    private boolean publish;
    private String name;

    private String country;
    private String city;
    private String address;
    private String postal_code;

    private String time_zone;
    private GeoCoordinates coordinates;
    private List<Evse> evses;
    private OpeningTimes opening_times;
    private String last_updated;
}
