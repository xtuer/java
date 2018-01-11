package com.xtuer.controller;

import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.PaperDirectory;
import com.xtuer.bean.Result;
import com.xtuer.mapper.PaperDirectoryMapper;
import com.xtuer.service.SnowflakeIdWorker;
import com.xtuer.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class PaperDirectoryController {
    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private PaperDirectoryMapper directoryMapper;

    /**
     * 获取所有的目录
     * URL: http://localhost:8080/rest/paperDirectories
     *
     * @return
     */
    @GetMapping(UriView.REST_PAPER_DIRECTORIES)
    @ResponseBody
    public Result<List<PaperDirectory>> getAllPaperDirectories() {
        return Result.ok("", directoryMapper.getAllPaperDirectories());
    }

    /**
     * 查找子目录
     * URL: http://localhost:8080/rest/paperDirectories/0/paperSubdirectories
     *
     * @param paperDirectoryId 目录的 id
     * @return
     */
    @GetMapping(UriView.REST_PAPER_SUBDIRECTORIES)
    @ResponseBody
    public Result<List<PaperDirectory>> findPaperSubdirectories(@PathVariable String paperDirectoryId) {
        return Result.ok("", directoryMapper.findPaperSubdirectories(paperDirectoryId));
    }

    /**
     * 创建子目录
     * URL: http://localhost:8080/rest/paperDirectories
     * 参数: name, parentPaperDirectoryId
     *
     * @param paperDirectory
     * @param bindingResult
     * @return
     */
    @PostMapping(UriView.REST_PAPER_DIRECTORIES)
    @ResponseBody
    public Result<PaperDirectory> createPaperDirectory(@Valid PaperDirectory paperDirectory, BindingResult bindingResult) {
        // 如有参数错误，则返回错误信息给客户端
        if (bindingResult.hasErrors()) {
            return Result.fail(CommonUtils.getBindingMessage(bindingResult));
        }

        paperDirectory.setPaperDirectoryId(idWorker.nextId() + "");
        directoryMapper.createPaperDirectory(paperDirectory);

        return Result.ok("", paperDirectory);
    }

    /**
     * 重命名目录
     * URL: http://localhost:8080/rest/paperDirectories/3/name
     *
     * @param paperDirectoryId 目录的 id
     * @param name 目录的新名字 name
     * @return
     */
    @PutMapping(UriView.REST_PAPER_DIRECTORY_NAME)
    @ResponseBody
    public Result renamePaperDirectory(@PathVariable String paperDirectoryId, @RequestParam String name) {
        if (name == null || name.trim().equals("")) {
            return Result.fail("目录名不能为空");
        }

        directoryMapper.renamePaperDirectory(paperDirectoryId, name);
        return Result.ok("重命名目录成功");
    }

    /**
     * 移动目录
     * URL: http://localhost:8080/rest/paperDirectories/3/parentPaperDirectoryId
     * 参数: name, parentPaperDirectoryId
     *
     * @param paperDirectoryId
     * @param parentPaperDirectoryId
     * @return
     */
    @PutMapping(UriView.REST_PAPER_DIRECTORY_PARENT_ID)
    @ResponseBody
    public Result changeParentPaperDirectoryId(@PathVariable String paperDirectoryId, @RequestParam String parentPaperDirectoryId) {
        directoryMapper.changeParentPaperDirectoryId(paperDirectoryId, parentPaperDirectoryId);
        return Result.ok("移动目录成功");
    }

    /**
     * 删除目录
     * URL: http://localhost:8080/rest/paperDirectories/3
     *
     * @param paperDirectoryId
     * @return
     */
    @DeleteMapping(UriView.REST_PAPER_DIRECTORIES_BY_ID)
    @ResponseBody
    public Result deletePaperDirectory(@PathVariable String paperDirectoryId) {
        if (directoryMapper.hasPaperSubdirectories(paperDirectoryId) || directoryMapper.hasPapers(paperDirectoryId)) {
            return Result.fail("不能删除非空目录");
        } else {
            directoryMapper.markPaperDirectoryAsDeleted(paperDirectoryId);
            return Result.ok();
        }
    }

    /**
     * 查询所有目录下试卷的数量
     * URL: http://localhost:8080/rest/paperDirectories/papersCount
     *
     * @return
     */
    @GetMapping(UriView.REST_PAPER_DIRECTORIES_PAPER_COUNTS)
    @ResponseBody
    public Result findPaperCountsInPaperDirectories() {
        List<Map<String, String>> counts = directoryMapper.findPaperCountsInPaperDirectories();
        return Result.ok("", counts);
    }

    /**
     * 查询目录下试卷的所有知识点
     * URL: http://localhost:8080/rest/paperDirectories/223/knowledgePoints
     *
     * @param paperDirectoryId
     * @return
     */
    @GetMapping(UriView.REST_PAPER_DIRECTORIES_KNOWLEDGE_POINTS)
    @ResponseBody
    public Result findKnowledgePointsInPaperDirectory(@PathVariable String paperDirectoryId) {
        List<KnowledgePoint> points = directoryMapper.findKnowledgePointsInPaperDirectory(paperDirectoryId);
        return Result.ok("", points);
    }
}
