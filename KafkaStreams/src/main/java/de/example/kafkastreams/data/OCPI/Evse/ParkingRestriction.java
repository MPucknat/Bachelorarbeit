package de.example.kafkastreams.data.OCPI.Evse;

import lombok.Getter;

@Getter
public enum ParkingRestriction {
    EV_ONLY,
    PLUGGED,
    DISABLED,
    CUSTOMERS,
    MOTORCYCLES,
}
