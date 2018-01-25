package com.xtuer.mapper;

import com.xtuer.bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    List<User> users();
    User userByNameAndCountry(@Param("name") String name, @Param("country") String country);
}
