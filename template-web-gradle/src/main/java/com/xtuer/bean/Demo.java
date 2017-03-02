package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Demo {
    private int id;
    private String info;

    public Demo() {
    }

    public Demo(int id, String info) {
        this.id = id;
        this.info = info;
    }
}
