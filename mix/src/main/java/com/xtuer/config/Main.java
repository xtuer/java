package com.xtuer.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("auto-config.xml");
        AutoConfig config = context.getBean("autoConfig", AutoConfig.class);
    }
}
