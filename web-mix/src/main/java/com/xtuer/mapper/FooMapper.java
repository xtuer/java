package com.xtuer.mapper;

import com.xtuer.bean.Foo;
import org.apache.ibatis.annotations.Param;

public interface FooMapper {
    Foo findFooById(int id);
    Foo findFooByNameAndCity(@Param("name") String name, @Param("city") String city);
}
