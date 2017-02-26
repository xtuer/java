package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Resource;

@Getter
@Setter
public class Company {
    @Resource(name = "admin")
    private Admin admin;
}
