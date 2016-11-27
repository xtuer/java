package com.xtuer.service;

public class HelloServiceImpl implements HelloService {
    String name;

    String hello() {
        return "Hello $name. Welcome to Groovy in Spring";
    }
}
