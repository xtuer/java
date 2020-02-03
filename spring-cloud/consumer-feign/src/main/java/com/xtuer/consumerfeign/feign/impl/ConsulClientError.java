package com.xtuer.consumerfeign.feign.impl;

import com.xtuer.consumerfeign.feign.ConsulClient;
import org.springframework.stereotype.Component;

@Component
public class ConsulClientError implements ConsulClient {
    @Override
    public String dc() {
        return "consul-client down!!!";
    }
}
