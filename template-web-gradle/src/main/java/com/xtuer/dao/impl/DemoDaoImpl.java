package com.xtuer.dao.impl;

import com.xtuer.dao.DemoDao;
import com.xtuer.mapper.DemoMapper;
import com.xtuer.bean.Demo;
import org.springframework.beans.factory.annotation.Autowired;

public class DemoDaoImpl implements DemoDao {
    @Autowired
    private DemoMapper mapper; // mybatis.xml 里配置 MapperScannerConfigurer 自动生成的对象

    @Override
    public Demo findDemoById(int id) {
        return mapper.findDemoById(id);
    }
}
