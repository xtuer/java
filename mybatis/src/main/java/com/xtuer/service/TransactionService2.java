package com.xtuer.service;

import com.xtuer.bean.Demo;
import com.xtuer.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService2 {
    @Autowired
    private DemoMapper mapper;

    private int demoId = 3;

    @Transactional(propagation= Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void selectData() {
        Demo demo = mapper.findDemoById(demoId);
        System.out.println("2. After Update: " + demo.getInfo());
    }
}
