package com.xtuer.converter;

import com.xtuer.bean.Admin;
import org.springframework.core.convert.converter.Converter;

public class AdminConverter implements Converter<String, Admin> {
    // "Bob, 123"
    @Override
    public Admin convert(String source) {
        String[] components = source.split(",");
        Admin admin = new Admin();
        admin.setUsername(components[0].trim());
        admin.setAge(Integer.parseInt(components[1].trim()));

        return admin;
    }
}
