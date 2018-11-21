package com.xtuer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class TokenWhiteListService {
    @Resource(name = "whiteList")
    private Set<String> whiteList;

    public void foo() {
        System.out.println(whiteList);
        System.out.println(whiteList.size());
    }
}
