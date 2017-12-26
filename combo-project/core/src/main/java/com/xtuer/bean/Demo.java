package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Demo {
    private int id = 0;
    private String info;

    public Demo() {

    }

    public Demo(int id, String info) {
        this.id = id;
        this.info = info;
    }
}
