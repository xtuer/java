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
    synchronized public int nextSequence(String sequenceName) {
        // 1. 序列号增加 1
        // 2. 获取最新的序列号
        commonMapper.increaseSequenceByName(sequenceName);
        return commonMapper.findSequenceByName(sequenceName);
    }
}
