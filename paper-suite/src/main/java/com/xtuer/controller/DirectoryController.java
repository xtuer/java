package com.xtuer.controller;

import com.xtuer.bean.Directory;
import com.xtuer.bean.Result;
import com.xtuer.mapper.DirectoryMapper;
import com.xtuer.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class DirectoryController {
    @Autowired
    private DirectoryMapper directoryMapper;

    /**
     * 查找父目录下的目录
     * URL: /rest/directories?parentDirectoryId=0
     *
     * @param parentDirectoryId 父目录的 id
     * @return
     */
    @GetMapping(UriView.REST_DIRECTORIES)
    @ResponseBody
    public Result<List<Directory>> subdirectories(@RequestParam long parentDirectoryId) {
        return Result.ok("", directoryMapper.findDirectoriesByParentDirectoryId(parentDirectoryId));
    }

    /**
     * 创建目录
     * URL: /rest/directories
     * 参数: name, parentDirectoryId
     *
     * @param directory
     * @param bindingResult
     * @return
     */
    @PostMapping(UriView.REST_DIRECTORIES)
    @ResponseBody
    public Result<Directory> createDirectory(@Valid Directory directory, BindingResult bindingResult) {
        // 如有参数错误，则返回错误信息给客户端
        if (bindingResult.hasErrors()) {
            return Result.fail(CommonUtils.getBindingMessage(bindingResult));
        }

        directory.setUuidName(CommonUtils.uuid());
        directoryMapper.createDirectory(directory);
        return Result.ok("", directory);
    }

    /**
     * 重命名目录
     * URL: /rest/directories/3/name
     *
     * @param directoryId 目录的 id
     * @param name 目录的新名字 name
     * @return
     */
    @PutMapping(UriView.REST_DIRECTORY_NAME)
    @ResponseBody
    public Result renameDirectory(@PathVariable long directoryId, @RequestParam String name) {
        if (name == null || name.trim().equals("")) {
            return Result.fail("目录名不能为空");
        }

        directoryMapper.renameDirectory(directoryId, name);
        return Result.ok("重命名目录成功");
    }

    /**
     * 移动目录
     * URL: /rest/directories/3/parentId
     * 参数: name, parentDirectoryId
     *
     * @param directoryId
     * @param parentDirectoryId
     * @return
     */
    @PutMapping(UriView.REST_DIRECTORY_PARENT_ID)
    @ResponseBody
    public Result changeParentDirectoryId(@PathVariable long directoryId, @RequestParam long parentDirectoryId) {
        directoryMapper.changeParentDirectoryId(directoryId, parentDirectoryId);
        return Result.ok("移动目录成功");
    }

    /**
     * 删除目录
     *
     * @param directoryId
     * @return
     */
    @DeleteMapping(UriView.REST_DIRECTORIES_WITH_ID)
    @ResponseBody
    public Result deleteDirectory(@PathVariable long directoryId) {
        if (directoryMapper.hasSubdirectories(directoryId)) {
            return Result.fail("不能删除非空目录");
        } else {
            directoryMapper.markDirectoryAsDeleted(directoryId);
            return Result.ok();
        }
    }
}