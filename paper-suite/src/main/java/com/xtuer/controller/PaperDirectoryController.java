package com.xtuer.controller;

import com.xtuer.bean.PaperDirectory;
import com.xtuer.bean.Result;
import com.xtuer.mapper.PaperDirectoryMapper;
import com.xtuer.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class PaperDirectoryController {
    @Autowired
    private PaperDirectoryMapper paperDirectoryMapper;

    /**
     * 查找子目录
     * URL: http://localhost:8080/rest/paperDirectories/0/paperSubdirectories
     *
     * @param paperDirectoryId 目录的 id
     * @return
     */
    @GetMapping(UriView.REST_PAPER_SUBDIRECTORIES)
    @ResponseBody
    public Result<List<PaperDirectory>> findPaperSubdirectories(@PathVariable long paperDirectoryId) {
        return Result.ok("", paperDirectoryMapper.findPaperSubdirectories(paperDirectoryId));
    }

    /**
     * 创建子目录
     * URL: http://localhost:8080/rest/paperDirectories/0/paperSubdirectories
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

        paperDirectory.setUuidName(CommonUtils.uuid());
        paperDirectoryMapper.createPaperDirectory(paperDirectory);

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
    public Result renamePaperDirectory(@PathVariable long paperDirectoryId, @RequestParam String name) {
        if (name == null || name.trim().equals("")) {
            return Result.fail("目录名不能为空");
        }

        paperDirectoryMapper.renamePaperDirectory(paperDirectoryId, name);
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
    public Result changeParentPaperDirectoryId(@PathVariable long paperDirectoryId, @RequestParam long parentPaperDirectoryId) {
        paperDirectoryMapper.changeParentPaperDirectoryId(paperDirectoryId, parentPaperDirectoryId);
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
    public Result deletePaperDirectory(@PathVariable long paperDirectoryId) {
        if (paperDirectoryMapper.hasPaperSubdirectories(paperDirectoryId) || paperDirectoryMapper.hasPapers(paperDirectoryId)) {
            return Result.fail("不能删除非空目录");
        } else {
            paperDirectoryMapper.markPaperDirectoryAsDeleted(paperDirectoryId);
            return Result.ok();
        }
    }
}
