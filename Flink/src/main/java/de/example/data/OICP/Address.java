package de.example.data.OICP;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Address {

    @JsonProperty("Country")
    private String country;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Street")
    private String street;

    @JsonProperty("PostalCode")
    private String postalCode;

    @JsonProperty("HouseNum")
    private String houseNum;

    @JsonProperty("TimeZone")
    private String timeZone;
}
