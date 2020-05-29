package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EB {
    private long   id;
    private String username;
    private Role   role;

    public enum Role {
        ADMIN, STUDENT, TEACHER
    }
}
