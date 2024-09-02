package de.example.data.finalLocation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String country;
    private String city;
    private String address;
    private String postalCode;
    private String timeZone;
}
