package com.xtuer.service;

import com.xtuer.bean.DiskFile;
import com.xtuer.bean.Page;
import com.xtuer.bean.Result;
import com.xtuer.bean.UploadedFile;
import com.xtuer.mapper.FileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.util.List;

/**
 * 网盘服务
 */
@Service
@Slf4j
public class DiskService extends BaseService {
    @Autowired
    private FileMapper fileMapper;

    /**
     * 查询用户 的文件，如果 userId 为 0，则查询所有用户的文件，对 filename 使用模糊匹配
     *
     * @param userId   用户 ID
     * @param filename 文件名
     * @param page     分页对象
     * @return 返回文件的数组
     */
    public List<DiskFile> findDiskFiles(long userId, String filename, Page page) {
        return fileMapper.findDiskFiles(userId, filename, page);
    }

    /**
     * 保存文件到网盘
     *
     * @param userId 用户 ID
     * @param fileId 文件 ID
     * @return payload 为操作的结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<DiskFile> saveFileToDisk(long userId, long fileId) {
        // 1. 查询文件
        // 2. 如果文件存在且是用户 userId 上传的，则移动到文件仓库且保存到 disk 表中

        // [1] 查询文件
        UploadedFile file = fileMapper.findUploadedFileById(fileId);

        if (file == null) {
            return Result.fail("文件 [{}] 不存在", fileId + "");
        }

        if (userId != file.getUserId()) {
            return Result.fail("只能保存自己上传的文件到网盘");
        }

        // [2] 如果文件存在且是用户 userId 上传的，则移动到文件仓库且保存到 disk 表中
        log.info("[开始] 保存文件到网盘，文件 ID [{}]", fileId);

        super.repoFileService.moveTempFileToRepo(file.getUrl());
        fileMapper.insertFileToDisk(fileId);

        log.info("[结束] 保存文件到网盘，文件 ID [{}]", fileId);

        return Result.ok(fileMapper.findDiskFileById(fileId));
    }

    /**
     * 删除网盘中的文件
     *
     * @param fileId 文件 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDiskFile(@PathVariable long fileId) {
        // 1. 查询文件
        // 2. 删除上传的文件记录
        // 3. 删除网盘中的文件记录
        // 4. 删除文件仓库中的文件

        // [1] 查询文件
        DiskFile diskFile = fileMapper.findDiskFileById(fileId);

        // 不存在立即返回
        if (diskFile == null) {
            return;
        }

        String url = diskFile.getUrl();

        log.info("[开始] 删除网盘文件 [{}], URL [{}]", diskFile.getFilename(), url);

        // [2] 删除上传的文件记录
        // [3] 删除网盘中的文件记录
        fileMapper.deleteUploadedFileById(fileId);
        fileMapper.deleteDiskFile(fileId);

        // [4] 删除文件仓库中的文件
        File repoFile = super.repoFileService.getRepoFileByUrl(url);
        super.repoFileService.deleteRepoFile(repoFile, url);

        log.info("[结束] 删除网盘文件 [{}], URL [{}]", diskFile.getFilename(), url);
    }
}
