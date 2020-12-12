package com.xtuer.controller;

import com.xtuer.bean.*;
import com.xtuer.service.DiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 网盘控制器
 */
@RestController
public class DiskController extends BaseController {
    @Autowired
    private DiskService diskService;

    /**
     * 查询用户 的文件，如果 userId 为 0，则查询所有用户的文件，对 filename 使用模糊匹配
     *
     * 网址: http://localhost:8080/api/disk/files
     * 参数:
     *      userId     [可选]: 用户 ID
     *      filename   [可选]: 文件名
     *      pageNumber [可选]: 页码
     *      pageSize   [可选]: 数量
     *
     * @param userId   用户 ID
     * @param filename 文件名
     * @param page     分页对象
     * @return 返回文件的数组
     */
    @GetMapping(Urls.API_DISK_FILES)
    public Result<List<DiskFile>> findDiskFiles(@RequestParam(required = false, defaultValue = "0") long userId,
                                                @RequestParam(required = false) String filename,
                                                Page page) {
        return Result.ok(diskService.findDiskFiles(userId, filename, page));
    }

    /**
     * 保存文件到网盘
     *
     * 网址: http://localhost:8080/api/disk/files
     * 参数: fileId (必要): 文件 ID
     *
     * @param fileId 文件 ID
     * @return payload 为操作的结果
     */
    @PostMapping(Urls.API_DISK_FILES)
    public Result<DiskFile> saveFileToDisk(long fileId) {
        long userId = super.getCurrentUserId();
        return diskService.saveFileToDisk(userId, fileId);
    }

    /**
     * 删除网盘中的文件
     *
     * 网址: http://localhost:8080/api/disk/files/{fileId}
     * 参数: 无
     *
     * @param fileId 文件 ID
     */
    @DeleteMapping(Urls.API_DISK_FILES_BY_ID)
    public Result<Boolean> deleteDiskFile(@PathVariable long fileId) {
        diskService.deleteDiskFile(fileId);
        return Result.ok();
    }
}
