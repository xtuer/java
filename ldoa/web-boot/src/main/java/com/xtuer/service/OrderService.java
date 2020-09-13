package com.xtuer.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 订单服务
 */
@Service
public class OrderService {
    @Autowired
    private CommonService commonService;

    /**
     * 生成订单号，格式为 XSDD-20200806-0001
     *
     * @return 返回订单号
     */
    public String generateOrderSn() {
        // XSDD 不动, 20200806 为年月日，根据日期自动生成, 0001 为流水号 (0001,0002,003……)，每年再从 0001 开始
        String year = DateTimeFormatter.ofPattern("yyyy").format(LocalDate.now());
        String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        String name = "ORDER-" + year;
        String sn   = commonService.nextSequence(name) + "";
        String orderSn = "XSDD-" + date + "-" + StringUtils.leftPad(sn, 4, "0");

        return orderSn;
    }
}
