package com.xtuer.mapper;

import com.xtuer.bean.Demo;

import java.util.List;

public interface DemoMapper {
    // 使用 id 查找 Demo
    Demo findDemoById(int id);

    // 使用 info 查找 Demos
    List<Demo> findDemosByInfo(String info);

    boolean hasDemo();

    int demoCount();

    // 插入 Demo
    int insertDemo(Demo demo);
}
