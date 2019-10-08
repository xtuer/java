package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Address {
    private String city;
    private String street;

    public Address() {}

    public Address(String city, String street) {
        this.city = city;
        this.street = street;
    }
}
