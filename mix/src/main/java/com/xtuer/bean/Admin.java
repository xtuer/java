package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Admin {
    private int id;
    private String name;

    public Admin() {

    }

    public Admin(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
