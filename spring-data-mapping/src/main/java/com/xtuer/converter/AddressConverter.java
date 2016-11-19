package com.xtuer.converter;

import com.xtuer.bean.Address;
import org.springframework.core.convert.converter.Converter;

public class AddressConverter implements Converter<String, Address> {
    // "Beijing, Caoyang"
    @Override
    public Address convert(String source) {
        String[] components = source.split(",");
        Address address = new Address();
        address.setCity(components[0].trim());
        address.setStreet(components[1].trim());

        return address;
    }
}
