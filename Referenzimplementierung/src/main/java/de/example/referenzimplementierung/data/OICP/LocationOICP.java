package de.example.referenzimplementierung.data.OICP;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationOICP {

    @JsonProperty("OperatorID")
    private String operatorID;

    @JsonProperty("OperatorName")
    private String operatorName;

    @JsonProperty("EvseID")
    private String evseID;

    @JsonProperty("ChargingPoolID")
    private String chargingPoolID;

    @JsonProperty("ChargingStationNames")
    private List<ChargingStationName> chargingStationNames;

    @JsonProperty("Address")
    private Address address;

    @JsonProperty("GeoCoordinates")
    private GeoCoordinates geoCoordinates;

    @JsonProperty("Plugs")
    private List<Plugs> plugs;

    @JsonProperty("ChargingFacilities")
    private ChargingFacilities chargingFacilities;

    @JsonProperty("RenewableEnergy")
    private boolean renewableEnergy;

    @JsonProperty("CalibrationLawDataAvailability")
    private CalibrationLawDataAvailability calibrationLawDataAvailability;

    @JsonProperty("AuthenticationModes")
    private List<AuthenticationMode> authenticationModes;

    @JsonProperty("PaymentOptions")
    private List<PaymentOption> paymentOptions;

    @JsonProperty("ValueAddedServices")
    private List<ValueAddedService> valueAddedServices;

    @JsonProperty("Accessibility")
    private Accessibility accessibility;

    @JsonProperty("HotlinePhoneNumber")
    private String hotlinePhoneNumber;

    @JsonProperty("IsOpen24Hours")
    private boolean isOpen24Hours;

    @JsonProperty("IsHubjectCompatible")
    private boolean isHubjectCompatible;

    @JsonProperty("DynamicInfoAvailable")
    private DynamicInfoAvailable dynamicInfoAvailable;

    private String lastUpdate;
}
