package com.xtuer.mapper;

import com.xtuer.bean.User;

public interface UserMapper {
    User userByName(String name);
}
