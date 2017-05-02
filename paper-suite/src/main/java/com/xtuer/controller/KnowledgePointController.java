package com.xtuer.controller;

import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Result;
import com.xtuer.mapper.KnowledgePointMapper;
import com.xtuer.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class KnowledgePointController {
    @Autowired
    private KnowledgePointMapper mapper;

    /**
     * 查找指定知识点分类下的所有知识点
     * URL: http://localhost:8080/rest/knowledgePointGroups/2/knowledgePoints
     *
     * @param knowledgePointGroupId 知识点分类的 id
     * @return
     */
    @GetMapping(UriView.REST_KNOWLEDGE_POINTS_OF_GROUP)
    @ResponseBody
    public Result<List<KnowledgePoint>> findKnowledgePointsByKnowledgePointGroupId(@PathVariable String knowledgePointGroupId) {
        return Result.ok("", mapper.findKnowledgePointsByKnowledgePointGroupId(knowledgePointGroupId));
    }

    /**
     * 创建知识点
     * URL: http://localhost:8080/rest/knowledgePoints
     * 参数: name, knowledgePointGroupId
     *
     * @param knowledgePoint
     * @param bindingResult
     * @return
     */
    @PostMapping(UriView.REST_KNOWLEDGE_POINTS)
    @ResponseBody
    public Result createKnowledgePoint(@Valid KnowledgePoint knowledgePoint, BindingResult bindingResult) {
        // 如有参数错误，则返回错误信息给客户端
        if (bindingResult.hasErrors()) {
            return Result.fail(CommonUtils.getBindingMessage(bindingResult));
        }

        knowledgePoint.setName(knowledgePoint.getName().trim());
        mapper.createKnowledgePoint(knowledgePoint);

        return Result.ok("", knowledgePoint);
    }

    /**
     * 修改知识点
     * URL: http://localhost:8080/rest/knowledgePoints/3
     * 参数: name, knowledgePointGroupId
     *
     * @param knowledgePointId
     * @param knowledgePoint
     * @param bindingResult
     * @return
     */
    @PutMapping(UriView.REST_KNOWLEDGE_POINTS_BY_ID)
    @ResponseBody
    public Result updateKnowledgePoint(@PathVariable String knowledgePointId,
                                       @Valid KnowledgePoint knowledgePoint, BindingResult bindingResult) {
        // 如有参数错误，则返回错误信息给客户端
        if (bindingResult.hasErrors()) {
            return Result.fail(CommonUtils.getBindingMessage(bindingResult));
        }

        knowledgePoint.setKnowledgePointId(knowledgePointId);
        knowledgePoint.setName(knowledgePoint.getName().trim());
        mapper.updateKnowledgePoint(knowledgePoint);

        return Result.ok();
    }

    /**
     * 删除知识点
     * URL: http://localhost:8080/rest/knowledgePoints/3
     *
     * @param knowledgePointId
     * @return
     */
    @DeleteMapping(UriView.REST_KNOWLEDGE_POINTS_BY_ID)
    @ResponseBody
    public Result deleteKnowledgePoint(@PathVariable String knowledgePointId) {
        mapper.markKnowledgePointAsDeleted(knowledgePointId);

        return Result.ok();
    }
}
