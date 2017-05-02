package com.xtuer.controller;

import com.xtuer.bean.KnowledgePointGroup;
import com.xtuer.bean.Result;
import com.xtuer.mapper.KnowledgePointGroupMapper;
import com.xtuer.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class KnowledgePointGroupController {
    @Autowired
    private KnowledgePointGroupMapper groupMapper;

    /**
     * 查找所有的 KnowledgePointGroup
     * URL: http://localhost:8080/rest/knowledgePointGroups
     */
    @GetMapping(UriView.REST_KNOWLEDGE_POINT_GROUPS)
    @ResponseBody
    public Result<List<KnowledgePointGroup>> findAllKnowledgePointGroups() {
        return Result.ok("", groupMapper.findAllKnowledgePointGroups());
    }

    /**
     * 创建 KnowledgePointGroup
     * URL: http://localhost:8080/rest/knowledgePointGroups
     * 参数: name
     *
     * @param group
     * @param bindingResult
     */
    @PostMapping(UriView.REST_KNOWLEDGE_POINT_GROUPS)
    @ResponseBody
    public Result createKnowledgePointGroup(@Valid KnowledgePointGroup group, BindingResult bindingResult) {
        // 如有参数错误，则返回错误信息给客户端
        if (bindingResult.hasErrors()) {
            return Result.fail(CommonUtils.getBindingMessage(bindingResult));
        }

        group.setName(group.getName().trim());
        groupMapper.createKnowledgePointGroup(group);

        return Result.ok("", group);
    }

    /**
     * 修改 KnowledgePointGroup
     * URL: http://localhost:8080/rest/knowledgePointGroups/1
     * 参数: name
     *
     * @param knowledgePointGroupId
     * @param group
     * @param bindingResult
     */
    @PutMapping(UriView.REST_KNOWLEDGE_POINT_GROUPS_BY_ID)
    @ResponseBody
    public Result updateKnowledgePointGroup(@PathVariable String knowledgePointGroupId,
                                            @Valid KnowledgePointGroup group, BindingResult bindingResult) {
        // 如有参数错误，则返回错误信息给客户端
        if (bindingResult.hasErrors()) {
            return Result.fail(CommonUtils.getBindingMessage(bindingResult));
        }

        group.setName(group.getName().trim());
        group.setKnowledgePointGroupId(knowledgePointGroupId);
        groupMapper.updateKnowledgePointGroup(group);

        return Result.ok();
    }

    /**
     * 删除 KnowledgePointGroup
     * URL: http://localhost:8080/rest/knowledgePointGroups/1
     *
     * @param knowledgePointGroupId
     */
    @DeleteMapping(UriView.REST_KNOWLEDGE_POINT_GROUPS_BY_ID)
    @ResponseBody
    public Result deleteKnowledgePointGroup(@PathVariable String knowledgePointGroupId) {
        groupMapper.markKnowledgePointGroupAsDeleted(knowledgePointGroupId);
        return Result.ok();
    }
}
