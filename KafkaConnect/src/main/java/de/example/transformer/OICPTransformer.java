package de.example.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.example.data.OICP.AuthenticationMode;
import de.example.data.OICP.LocationOICP;
import de.example.data.OICP.PaymentOption;
import de.example.data.OICP.ValueAddedService;
import de.example.data.finalLocation.*;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OICPTransformer<R extends ConnectRecord<R>> implements Transformation<R>{

    @Override
    public void configure(Map<String, ?> map) {
    }

    @Override
    public R apply(R record) {
        ObjectMapper mapper = new ObjectMapper();
        Object recordObject = record.value();

        LocationOICP location;
        try {
            location = mapper.readValue((String) recordObject, new TypeReference<LocationOICP>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        LocationFinal finalLocation = LocationFinal.builder()
                .operatorID(location.getOperatorID())
                .operatorName(location.getOperatorName())
                .poolID(location.getChargingPoolID())
                .open24Hours(location.isOpen24Hours())
                .lastUpdate(location.getLastUpdate())
                .address(Address.builder()
                        .country(location.getAddress().getCountry())
                        .city(location.getAddress().getCity())
                        .address(location.getAddress().getStreet() + " " + location.getAddress().getHouseNum())
                        .postalCode(location.getAddress().getPostalCode())
                        .timeZone(location.getAddress().getTimeZone())
                        .build())
                .coordinates(GeoCoordinates.builder()
                        .latitude(location.getGeoCoordinates().getLatitude())
                        .longitude(location.getGeoCoordinates().getLongitude())
                        .build())
                .evses(getEvses(location))
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

    private static List<Evse> getEvses(LocationOICP locationOICP) {
        List<Evse> evses = new ArrayList<>();

        evses.add(Evse.builder()
                .evseID(locationOICP.getEvseID())
                .connectors(getConnectors(locationOICP))
                .capabilities(getCapabilities(locationOICP.getAuthenticationModes(), locationOICP.getPaymentOptions(), locationOICP.getValueAddedServices()))
                .accessibility(locationOICP.getAccessibility().toString())
                .build());

        return evses;
    }

    private static List<Connector> getConnectors(LocationOICP locationOICP) {
        List<Connector> connectors = new ArrayList<>();
        connectors.add(Connector.builder()
                .amperage(locationOICP.getChargingFacilities().getAmperage())
                .voltage(locationOICP.getChargingFacilities().getAmperage())
                .power(locationOICP.getChargingFacilities().getPower())
                .powerType(locationOICP.getChargingFacilities().getPowerType().toString())
                .build());

        return connectors;
    }

    private static List<String> getCapabilities(List<AuthenticationMode> authenticationModes, List<PaymentOption> paymentOptions, List<ValueAddedService> valueAddedServices) {
        List<String> capabilities = new ArrayList<>();

        capabilities.addAll(authenticationModes.stream().map(AuthenticationMode::toString).collect(Collectors.toList()));
        capabilities.addAll(paymentOptions.stream().map(PaymentOption::toString).collect(Collectors.toList()));
        capabilities.addAll(valueAddedServices.stream().map(ValueAddedService::toString).collect(Collectors.toList()));

        return capabilities;
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef();
    }

    @Override
    public void close() {
    }


}