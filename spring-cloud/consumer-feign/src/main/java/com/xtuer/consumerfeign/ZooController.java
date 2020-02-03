package com.xtuer.consumerfeign;

import com.xtuer.consumerfeign.feign.ConsulClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZooController {
    @Autowired
    private ConsulClient consulClient;

    @GetMapping("/feign/dc")
    public String dc() {
        return consulClient.dc();
    }
}
