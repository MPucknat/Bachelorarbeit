package de.example.data.OICP;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonValue;

@Getter
@AllArgsConstructor
public enum AuthenticationMode {
    NFC_RFID_CLASSIC("NFC RFID Classic"),
    NFC_RFID_DESFIRE("NFC RFID DESFire"),
    PNC("PnC"),
    REMOTE("Remote"),
    DIRECT_PAYMENT("Direct Payment"),
    NO_AUTHENTICATION_REQUIRED("No Authentication Required");

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
