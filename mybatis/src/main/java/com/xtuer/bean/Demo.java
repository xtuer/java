package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
public class Demo {
    private int id;
    private String info;
    private boolean marked;
    private Date createdAt = new Date();
    private Instant instant = Instant.now();
    private LocalDateTime localDateTime = LocalDateTime.now();
    private ZonedDateTime zonedDateTime = ZonedDateTime.now();
}
