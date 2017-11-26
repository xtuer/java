package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Gift {
    private int id;
    private String name;
    private String info;
    private int code;

    public Gift() {
    }

    public Gift(int id, String name, String info, int code) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.code = code;
    }

    @Override
    public String toString() {
        return "Gift{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", code=" + code +
                '}';
    }
}
