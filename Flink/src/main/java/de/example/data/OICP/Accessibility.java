package de.example.data.OICP;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonValue;

@Getter
@AllArgsConstructor
public enum Accessibility {
    FREE_PUBLICLY_ACCESSIBLE("Free publicly accessible"),
    RESTRICTED_ACCESS("Restricted access"),
    PAYING_PUBLICLY_ACCESSIBLE("Paying publicly accessible"),
    TEST_STATION("Test Station");

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
