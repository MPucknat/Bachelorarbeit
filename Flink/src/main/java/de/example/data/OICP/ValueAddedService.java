package de.example.data.OICP;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonValue;

@Getter
@AllArgsConstructor
public enum ValueAddedService {
    RESERVATION("Reservation"),
    DYNAMIC_PRICING("DynamicPricing"),
    PARKING_SENSORS("MaximumPowerCharging"),
    PREDICTIVE_CHARGE_POINT_USAGE("PredictiveChargePointUsage"),
    CHARGING_PLANS("ChargingPlans"),
    ROOF_PROVIDED("RoofProvided"),
    NONE("None");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
