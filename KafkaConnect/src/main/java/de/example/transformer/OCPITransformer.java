package de.example.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.example.data.OCPI.LocationOCPI;
import de.example.data.finalLocation.*;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;

import static java.util.stream.Collectors.toList;

public class OCPITransformer<R extends ConnectRecord<R>> implements Transformation<R> {

    @Override
    public void configure(Map<String, ?> map) {
    }

    @Override
    public R apply(R record) {
        ObjectMapper mapper = new ObjectMapper();
        Object recordObject = record.value();

        LocationOCPI location;
        try {
            location = mapper.readValue((String) recordObject, new TypeReference<LocationOCPI>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        LocationFinal finalLocation = LocationFinal.builder()
                .operatorID(location.getParty_id())
                .poolID(location.getId())
                .operatorName(location.getCountry_code() + "*" + location.getParty_id())
                .open24Hours(location.getOpening_times().isTwentyfourseven())
                .lastUpdate(location.getLast_updated())
                .address(Address.builder()
                        .country(location.getCountry())
                        .city(location.getCity())
                        .address(location.getAddress())
                        .postalCode(location.getPostal_code())
                        .timeZone(location.getTime_zone())
                        .build())
                .coordinates(GeoCoordinates.builder()
                        .latitude(location.getCoordinates().getLatitude())
                        .longitude(location.getCoordinates().getLongitude()).build())
                .evses(location.getEvses().stream().map(evse -> Evse.builder()
                        .evseID(evse.getEvse_id())
                        .connectors(evse.getConnectors().stream().map(connector -> Connector.builder()
                                .powerType(connector.getPower_type().toString())
                                .voltage(connector.getMax_voltage())
                                .amperage(connector.getMax_amperage())
                                .power(connector.getMax_electric_power())
                                .build()).collect(toList()))
                        .capabilities(evse.getCapabilities().stream().map(Enum::toString).collect(toList()))
                        .accessibility(evse.getParking_restrictions().toString()).build()).collect(toList()))
                .build();

        JsonNode finalLocationJson = mapper.valueToTree(finalLocation);
        String finalLocationString = finalLocationJson.toString();

        return record.newRecord(
                record.topic(),
                record.kafkaPartition(),
                record.keySchema(),
                record.key(),
                record.valueSchema(),
                finalLocationString,
                record.timestamp()
        );
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef();
    }

    @Override
    public void close() {

    }
}
