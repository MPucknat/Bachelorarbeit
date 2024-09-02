package de.example.restdatasender.data.OCPI.Evse.Connector;

import lombok.Getter;

@Getter
public enum ConnectorPowerType {
    AC_1_PHASE,
    AC_2_PHASE,
    AC_2_PHASE_SPLIT,
    AC_3_PHASE,
    DC,
}
