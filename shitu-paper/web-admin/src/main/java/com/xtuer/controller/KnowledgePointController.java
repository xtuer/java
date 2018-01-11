package com.xtuer.controller;

import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Result;
import com.xtuer.mapper.KnowledgePointMapper;
import com.xtuer.service.SnowflakeIdWorker;
import com.xtuer.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class KnowledgePointController {
    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private KnowledgePointMapper mapper;

    /**
     * 查找指定知识点分类下的所有知识点
     * URL: http://localhost:8080/rest/knowledgePoints
     * 参数: parentKnowledgePointId, type
     * type 为 0 时表示非叶节点，为 1 时表示 叶节点
     *
     * @param parentKnowledgePointId 知识点分类的 parent id
     * @param type
     * @return
     */
    @GetMapping(UriView.REST_KNOWLEDGE_POINTS)
    @ResponseBody
    public Result<List<KnowledgePoint>> findKnowledgePoints(@RequestParam String parentKnowledgePointId, @RequestParam int type) {
        return Result.ok("", mapper.findKnowledgePoints(parentKnowledgePointId, type));
    }

    /**
     * 查找所有的知识点分类
     * URL: http://localhost:8080/rest/knowledgePointGroups
     *
     * @return
     */
    @GetMapping(UriView.REST_KNOWLEDGE_POINT_GROUPS)
    @ResponseBody
    public Result<List<KnowledgePoint>> findAllKnowledgePointGroups() {
        return Result.ok("", mapper.findAllKnowledgePointGroups());
    }

    /**
     * 查找分类下的知识点
     * URL: http://localhost:8080/rest/knowledgePointGroups/{knowledgePointGroupId}/knowledgePoints
     *
     * @param knowledgePointGroupId 知识点分类的 id
     * @return
     */
    @GetMapping(UriView.REST_KNOWLEDGE_POINTS_IN_GROUP)
    @ResponseBody
    public Result findKnowledgePointsInGroup(@PathVariable String knowledgePointGroupId) {
        return Result.ok("", mapper.findKnowledgePointsInGroup(knowledgePointGroupId));
    }

    /**
     * 创建知识点
     * URL: http://localhost:8080/rest/knowledgePoints
     * 参数: name, parentKnowledgePointId, type
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

        knowledgePoint.setKnowledgePointId(idWorker.nextId() + "");
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
     * 删除知识点或分类
     * URL: http://localhost:8080/rest/knowledgePoints/3
     *
     * @param knowledgePointId
     * @return
     */
    @DeleteMapping(UriView.REST_KNOWLEDGE_POINTS_BY_ID)
    @ResponseBody
    public Result deleteKnowledgePoint(@PathVariable String knowledgePointId) {
        if (mapper.hasPapers(knowledgePointId) || mapper.hasKnowledgePoints(knowledgePointId)) {
            return Result.fail("它下面有分类、知识点或则试卷，不能删除");
        }

        mapper.deleteKnowledgePoint(knowledgePointId);

        return Result.ok();
    }

    /**
     * 重命名知识点
     *
     * @param knowledgePointId 知识点的 id
     * @param name 知识点的新名字
     * @return
     */
    @PutMapping(UriView.REST_KNOWLEDGE_POINTS_NAME)
    @ResponseBody
    public Result renameKnowledgePoint(@PathVariable String knowledgePointId, @RequestParam String name) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            return Result.fail("名字不能为空");
        }

        mapper.renameKnowledgePoint(knowledgePointId, name);
        return Result.ok();
    }


    /**
     * 移动知识点到其他分类
     * URL: http://localhost:8080/rest/knowledgePoints/{knowledgePointId}/parentKnowledgePointId
     * 参数: newParentKnowledgePointId
     *
     * @param knowledgePointId
     * @param newParentKnowledgePointId
     * @return
     */
    @PutMapping(UriView.REST_KNOWLEDGE_POINTS_PARENT_ID)
    @ResponseBody
    public Result reparentKnowledgePoint(@PathVariable String knowledgePointId, @RequestParam String newParentKnowledgePointId) {
        newParentKnowledgePointId = StringUtils.trim(newParentKnowledgePointId);

        if (StringUtils.isBlank(newParentKnowledgePointId)) {
            return Result.fail("分类不能为空");
        }

        mapper.reparentKnowledgePoint(knowledgePointId, newParentKnowledgePointId);
        return Result.ok();
    }
}
