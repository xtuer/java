package com.xtuer.controller;

import com.xtuer.bean.Area;
import com.xtuer.mapper.AreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AreaController {
    @Autowired
    private AreaMapper areaMapper;

    @GetMapping("/areas-of/{parentId}")
    @ResponseBody
    public List<Area> findAreasByParentId(@PathVariable int parentId) {
        return areaMapper.findAreasByParentId(parentId);
    }
}
