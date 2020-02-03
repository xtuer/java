package com.xtuer.consumerfeign.feign;

import com.xtuer.consumerfeign.feign.impl.ConsulClientError;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "consul-client", fallback = ConsulClientError.class)
public interface ConsulClient {
    // 方法和服务提供方的 Controller 中此请求的方法一样即可
    @GetMapping("/dc")
    String dc();
}
