package de.example.kafkastreams.kafka;

import de.example.kafkastreams.data.OCPI.LocationOCPI;
import de.example.kafkastreams.data.OICP.AuthenticationMode;
import de.example.kafkastreams.data.OICP.LocationOICP;
import de.example.kafkastreams.data.OICP.PaymentOption;
import de.example.kafkastreams.data.OICP.ValueAddedService;
import de.example.kafkastreams.data.finalLocation.*;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class StreamTransformer {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {

        KStream<String, LocationOCPI> ocpiStream = streamsBuilder
                .stream("ocpi-topic", Consumed.with(Serdes.String(), new JsonSerde<>(LocationOCPI.class)));

        KStream<String, LocationOICP> oicpStream = streamsBuilder
                .stream("oicp-topic", Consumed.with(Serdes.String(), new JsonSerde<>(LocationOICP.class)));

        KStream<String, LocationFinal> transformedOCPIStream = ocpiStream.mapValues(value -> LocationFinal.builder()
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
                .build());

        KStream<String, LocationFinal> transformedOICPStream = oicpStream.mapValues(value -> LocationFinal.builder()
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
                .build());

        transformedOCPIStream.to("output-topic");
        transformedOICPStream.to("output-topic");
    }

    private List<String> getCapabilities(List<AuthenticationMode> authenticationModes, List<PaymentOption> paymentOptions, List<ValueAddedService> valueAddedServices) {
        List<String> capabilities = new ArrayList<>();

        capabilities.addAll(authenticationModes.stream().map(AuthenticationMode::toString).toList());
        capabilities.addAll(paymentOptions.stream().map(PaymentOption::toString).toList());
        capabilities.addAll(valueAddedServices.stream().map(ValueAddedService::toString).toList());

        return capabilities;
    }
}
