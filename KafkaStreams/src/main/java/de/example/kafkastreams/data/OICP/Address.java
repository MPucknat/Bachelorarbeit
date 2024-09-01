package de.example.kafkastreams.data.OICP;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
