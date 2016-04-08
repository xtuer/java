package com.xtuer.domain;

public class Demo {
    private int id;
    private String description;

    public Demo() {
    }

    public Demo(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
