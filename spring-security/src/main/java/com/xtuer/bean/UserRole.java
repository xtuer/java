package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRole {
    private int id;
    private String role;

    public UserRole() {
    }

    public UserRole(String role) {
        this.role = role;
    }
}
