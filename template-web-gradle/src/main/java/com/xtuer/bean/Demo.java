package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.DeleteMapping;

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
