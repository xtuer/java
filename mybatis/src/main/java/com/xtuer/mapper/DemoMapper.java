package com.xtuer.mapper;

import com.xtuer.bean.Demo;

import java.util.List;

public interface DemoMapper {
    Demo findDemoById(int id);
    int insertDemo(Demo demo);
    List<Demo> findDemosByInfo(String info);
    boolean hasDemo();
    int demoCount();
}
