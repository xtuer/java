package com.xtuer.mapper;

import com.xtuer.bean.Area;

import java.util.List;

public interface AreaMapper {
    List<Area> findAreasByParentId(int parentId);
}
