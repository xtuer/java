package com.xtuer.service;

import com.xtuer.bean.Demo;
import com.xtuer.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    @Autowired
    private DemoMapper mapper;

    @Autowired
    private TransactionService2 service2;

    private int demoId = 3;

    @Transactional(rollbackFor = Exception.class)
    public void updateData() {
        String info = System.currentTimeMillis() + "";
        System.out.println("1. Before Update: " + info);

        Demo demo = new Demo();
        demo.setId(demoId);
        demo.setInfo(info);
        mapper.updateDemo(demo);

        try {
            // updateDataRollback();
        } catch (Exception ex) {

        }
        selectData(); // 调用同一个对象的方法，所以 selectData() 没有开启新的事务
        service2.selectData(); // 因为是不同对象，故开启新事务

        // throw new RuntimeException("MyBatis rollback");
    }

    @Transactional(propagation= Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateDataRollback() {
        Demo demo = new Demo();
        demo.setId(demoId);
        demo.setInfo("for rollback");
        mapper.updateDemo(demo);

        throw new RuntimeException("MyBatis rollback");
    }

    @Transactional(propagation= Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void selectData() {
        Demo demo = mapper.findDemoById(demoId);
        System.out.println("1. After Update: " + demo.getInfo());
    }
}
