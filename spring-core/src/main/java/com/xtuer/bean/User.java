package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class User {
    private String username;

    public User(String username) {
        this.username = username;
    }
}
