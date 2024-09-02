package de.example.referenzimplementierung;

import de.example.referenzimplementierung.data.OCPI.LocationOCPI;
import de.example.referenzimplementierung.data.OICP.*;
import de.example.referenzimplementierung.data.finalLocation.*;
import de.example.referenzimplementierung.data.finalLocation.Address;
import de.example.referenzimplementierung.data.finalLocation.GeoCoordinates;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RestEndpoint {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String SERVER_ADDRESS = "http://localhost:9090";
    private int ocpiOffset = 0;
    private int oicpOffset = 0;

    @GetMapping("/ocpi")
    public List<LocationFinal> transformOCPI() {
        List<LocationOCPI> allLocations = new ArrayList<>();

        while(true) {
            ResponseEntity<List<LocationOCPI>> response = restTemplate.exchange(SERVER_ADDRESS + "/ocpi/" + ocpiOffset, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

            if(response.getStatusCode() == HttpStatus.NO_CONTENT || !response.hasBody() || response.getBody() == null){
                break;
            }

            List<LocationOCPI> locations = response.getBody();
            allLocations.addAll(response.getBody());
            ocpiOffset += locations.size();
        }

        return allLocations.stream().map(value -> LocationFinal.builder()
                .operatorID(value.getParty_id())
                .poolID(value.getId())
                .operatorName(value.getCountry_code() + "*" + value.getParty_id())
                .open24Hours(value.getOpening_times().isTwentyfourseven())
                .lastUpdate(value.getLast_updated())
                .address(Address.builder()
                        .country(value.getCountry())
                        .city(value.getCity())
                        .address(value.getAddress())
                        .postalCode(value.getPostal_code())
                        .timeZone(value.getTime_zone())
                        .build())
                .coordinates(GeoCoordinates.builder()
                        .latitude(value.getCoordinates().getLatitude())
                        .longitude(value.getCoordinates().getLongitude()).build())
                .evses(value.getEvses().stream().map(evse -> Evse.builder()
                        .evseID(evse.getEvse_id())
                        .connectors(evse.getConnectors().stream().map(connector -> Connector.builder()
                                .powerType(connector.getPower_type().toString())
                                .voltage(connector.getMax_voltage())
                                .amperage(connector.getMax_amperage())
                                .power(connector.getMax_electric_power())
                                .build()).toList())
                        .capabilities(evse.getCapabilities().stream().map(Enum::toString).toList())
                        .accessibility(evse.getParking_restrictions().toString()).build()).toList())
                .build()).toList();
    }

    @GetMapping("/oicp")
    public List<LocationFinal> transformOICP() {
        List<LocationOICP> allLocations = new ArrayList<>();

        while(true) {
            ResponseEntity<List<LocationOICP>> response = restTemplate.exchange(SERVER_ADDRESS + "/oicp/" + oicpOffset, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

            if(response.getStatusCode() == HttpStatus.NO_CONTENT || !response.hasBody() || response.getBody() == null){
                break;
            }

            List<LocationOICP> locations = response.getBody();
            allLocations.addAll(response.getBody());
            oicpOffset += locations.size();
        }

        return allLocations.stream().map(value -> LocationFinal.builder()
                .operatorID(value.getOperatorID())
                .operatorName(value.getOperatorName())
                .poolID(value.getChargingPoolID())
                .open24Hours(value.isOpen24Hours())
                .lastUpdate(value.getLastUpdate())
                .address(Address.builder()
                        .country(value.getAddress().getCountry())
                        .city(value.getAddress().getCity())
                        .address(value.getAddress().getStreet() + " " + value.getAddress().getHouseNum())
                        .postalCode(value.getAddress().getPostalCode())
                        .timeZone(value.getAddress().getTimeZone())
                        .build())
                .coordinates(GeoCoordinates.builder()
                        .latitude(value.getGeoCoordinates().getLatitude())
                        .longitude(value.getGeoCoordinates().getLongitude())
                        .build())
                .evses(List.of(Evse.builder()
                        .evseID(value.getEvseID())
                        .connectors(List.of(Connector.builder()
                                .powerType(value.getChargingFacilities().getPowerType().toString())
                                .voltage(value.getChargingFacilities().getVoltage())
                                .amperage(value.getChargingFacilities().getAmperage())
                                .power(value.getChargingFacilities().getPower())
                                .build()))
                        .capabilities(getCapabilities(value.getAuthenticationModes(), value.getPaymentOptions(), value.getValueAddedServices()))
                        .accessibility(value.getAccessibility().toString())
                        .build()))
                .build()).toList();
    }

    private List<String> getCapabilities(List<AuthenticationMode> authenticationModes, List<PaymentOption> paymentOptions, List<ValueAddedService> valueAddedServices) {
        List<String> capabilities = new ArrayList<>();

        capabilities.addAll(authenticationModes.stream().map(AuthenticationMode::toString).toList());
        capabilities.addAll(paymentOptions.stream().map(PaymentOption::toString).toList());
        capabilities.addAll(valueAddedServices.stream().map(ValueAddedService::toString).toList());

        return capabilities;
    }
}
