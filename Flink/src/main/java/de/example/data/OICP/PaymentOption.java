package de.example.data.OICP;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonValue;

@Getter
@AllArgsConstructor
public enum PaymentOption {
    NO_PAYMENT("No Payment"),
    DIRECT("Direct"),
    CONTRACT("Contract");

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
