package com.xtuer.bean;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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
