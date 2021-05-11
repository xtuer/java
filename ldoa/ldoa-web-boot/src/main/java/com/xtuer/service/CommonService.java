package com.xtuer.service;

import com.xtuer.mapper.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通用服务
 */
@Service
public class CommonService {
    @Autowired
    private CommonMapper commonMapper;

    /**
     * 获取下一个序列号
     *
     * @param sequenceName 序列号名字
     * @return 返回下一个序列号
     */
    @Transactional(rollbackFor = Exception.class)
    public int nextSequence(String sequenceName) {
        // 1. 获得序列化的锁
        // 2. 序列号不存在则创建，否则自增 1
        // 3. 返回序列号

        Integer sn = commonMapper.lockSequenceByName(sequenceName);

        if (sn == null) {
            commonMapper.createSequence(sequenceName, 1);
            return 1;
        } else {
            commonMapper.increaseSequenceByOne(sequenceName);
            return sn + 1;
        }
    }
}
