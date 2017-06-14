package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
public class Admin {
    private int id;
    private String name;
    private Date date;

    public Admin() {

    }

    public Admin(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
