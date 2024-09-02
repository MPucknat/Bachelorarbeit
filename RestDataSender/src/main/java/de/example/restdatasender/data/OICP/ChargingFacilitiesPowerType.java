package de.example.restdatasender.data.OICP;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChargingFacilitiesPowerType {
    AC_1_PHASE("AC_1_PHASE"),
    AC_3_PHASE("AC_3_PHASE"),
    DC("DC");

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
