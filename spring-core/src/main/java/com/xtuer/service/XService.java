package com.xtuer.service;

import com.xtuer.annotation.Lock;
import com.xtuer.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class XService {
    // 1. SpEL 中没有参数: noom 作为锁名
    @Lock("noom")
    public void noom() {
        log.debug("XService::noom()");
    }

    // 2. SpEL 用简单参数: 参数 id 作为锁名
    @Lock("#id")
    public void foo(int id) {
        log.info("XService::foo()");
    }

    // 3. SpEL 用级联参数: user.username 作为锁名
    @Lock("#user.username")
    public void bar(User user) {
        log.info("XService::bar()");
    }

    // 4. SpEL 字符串拼接: lock- 后跟上参数 id 作为锁名
    @Lock("'lock-' + #id")
    public void goo(int id) {
        log.info("XService::goo()");
    }
}
