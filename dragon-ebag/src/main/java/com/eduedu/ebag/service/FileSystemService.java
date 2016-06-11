package com.eduedu.ebag.service;

import com.eduedu.ebag.bean.Result;
import com.eduedu.ebag.bean.filesystem.File;
import com.eduedu.ebag.mapper.filesystem.FileMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileSystemService {
    private static Logger logger = LoggerFactory.getLogger(FileSystemService.class);

    public static final String ACTION_RENAME = "rename";

    @Autowired
    private FileMapper fileMapper;

    /**
     * 移动文件或文件夹到 directoryId 表示的文件夹里:
     * 1. 文件夹不能移动到自己里面, 否则文件夹的父文件夹就是自己, 形成了一个环
     * 2. 要移动的文件或文件夹必须存在
     * 3. 已经在目标文件夹下, 不需要移动
     * 4. 目标文件夹必须存在, 找不到并且不是根文件夹, 则说明目标文件夹不存在, 根文件夹是逻辑文件夹, 数据库中没有对应的记录
     * 5. directoryId 必须是文件夹的 id
     * 6. 不能移动到子文件夹中, 否则文件夹的父文件夹就是它的子文件夹, 形成了一个环
     *
     * @param userId 用户的 id
     * @param fileId 文件的 id
     * @param directoryId 文件夹的 id
     * @return
     */
    public Result moveFileToDirectory(int userId, int fileId, int directoryId) {
        File file = fileMapper.selectFileByFileId(userId, fileId);
        File directory = fileMapper.selectFileByFileId(userId, directoryId);

        // 1. 不能移动到自己里面
        if (fileId == directoryId) {
            logger.info("不能移动到自己里面. directoryId: {}", fileId);
            return new Result(false, "不能移动到自己里面");
        }

        // 2. 要移动的文件或文件夹必须存在
        if (file == null) {
            logger.info("要移动的文件或文件夹不存在. fileId: {}", fileId);
            return new Result(false, "要移动的文件或文件夹不存在");
        }

        // 3. 已经在目标文件夹下, 不需要移动
        if (file.getDirectoryId() == directoryId) {
            logger.info("已经在此文件夹下, 不需要移动. fileId: {}", fileId);
            return new Result(false, "已经在此文件夹下, 不需要移动");
        }

        // 4. 目标文件夹必须存在, 找不到并且不是根文件夹, 则说明目标文件夹不存在, 根文件夹是逻辑文件夹, 数据库中没有对应的记录
        if (File.ROOT_DIRECTORY_ID != directoryId && directory == null) {
            logger.info("文件夹不存在. directoryId: {}", directoryId);
            return new Result(false, "文件夹不存在");
        }

        // 5. directoryId 必须是文件夹的 id
        if (directory != null && !directory.isDirectory()) {
            logger.info("不能移动到文件上. directoryId: {}", directoryId);
            return new Result(false, "不能移动到文件上");
        }

        // 6. 不能移动到子文件夹中
        if (isParentDirectory(userId, fileId, directoryId)) {
            logger.info("不能移动到自己的子文件夹里. fileId: {}, directoryId: {}", fileId, directoryId);
            return new Result(false, "不能移动到自己的子文件夹里");
        }

        // 执行移动操作
        fileMapper.moveFileToDirectory(userId, fileId, directoryId);
        logger.info("移动文件 '{}' 到文件夹 '{}'", fileId, directoryId);

        return new Result(true, "移动成功");
    }

    /**
     * 重命名文件为 displayName, 文件名不能为空
     *
     * @param userId 用户的 id
     * @param fileId 文件的 id
     * @param displayName 文件的新名字
     * @return
     */
    public Result renameFile(int userId, int fileId, String displayName) {
        if (StringUtils.isBlank(displayName)) {
            logger.info("文件名不能为空. fileId: {}", fileId);
            return new Result(false, "文件名不能为空");
        }

        fileMapper.renameFile(userId, fileId, displayName);
        logger.info("重命名文件 '{}' 为 '{}'", fileId, displayName);

        return new Result(true, "重命名成功");
    }

    /**
     * 在 file_id 为 parentDirectoryId 的文件夹下创建子文件夹
     *
     * @param userId 用户的 id
     * @param parentDirectoryId 父文件夹的 id
     * @param displayName 新建文件夹的名字
     * @return
     */
    public Result createDirectory(int userId, int parentDirectoryId, String displayName) {
        if (StringUtils.isBlank(displayName)) {
            logger.info("文件夹的名字不能为空");
            return new Result(false, "文件夹的名字不能为空");
        }

        displayName = StringUtils.trim(displayName); // 去掉空格

        if (parentDirectoryId != File.ROOT_DIRECTORY_ID) {
            File parentDirectory = fileMapper.selectFileByFileId(userId, parentDirectoryId);

            if (parentDirectory == null) {
                logger.info("父文件夹不存在. parentDirectoryId: {}", parentDirectoryId);
                return new Result(false, "父文件夹不存在");
            }

            if (!parentDirectory.isDirectory()) {
                logger.info("'{}' 不是文件夹, 不能在它里面创建文件夹", parentDirectoryId);
                return new Result(false, parentDirectory + " 不是文件夹, 不能在它里面创建文件夹");
            }
        }

        // 允许同名目录存在, 需要不同名字, 后期修改就可以了
        File directory = new File(displayName, "", parentDirectoryId, File.TYPE_DIRECTORY, userId);
        fileMapper.insertFile(directory);
        logger.info("在文件夹 '{}' 下创建文件夹 '{}'", parentDirectoryId, displayName);

        return new Result(true, directory, "创建文件夹成功");
    }

    /**
     * 删除文件(只是标记为删除, 并没有从数据库里物理删除)
     *
     * @param userId 用户的 id
     * @param fileId 文件的 id
     * @return
     */
    public Result deleteFile(int userId, int fileId) {
        int count = fileMapper.filesCountOfDirectory(userId, fileId);

        if (count > 0) {
            logger.info("不能删除非空文件夹");
            return new Result(false, "不能删除非空文件夹");
        }

        fileMapper.markFileAsDeleted(userId, fileId);
        logger.info("标记文件 {} 为删除", fileId);

        return new Result(true, "删除成功");
    }

    /**
     * shared 为 true 时标记文件为共享的, shared 为 false 时取消文件的共享
     *
     * @param userId 用户的 id
     * @param fileId 文件的 id
     * @param shared 是否共享的标志
     * @return
     */
    public Result shareFile(int userId, int fileId, boolean shared) {
        fileMapper.shareFile(userId, fileId, shared);
        logger.info(shared ? "共享" : "取消共享" + "文件 {}", fileId);

        return new Result(true, shared ? "共享文件成功" : "取消文件共享成功");
    }

    /**
     * 从下向上查找, 判断 directoryId 是否 fileId 的父文件夹 的 id
     *
     * @param userId 用户的 id
     * @param directoryId 文件夹的 id
     * @param fileId 文件的 id
     * @return 如果 directoryId 表示的文件夹是 fileId 表示的文件的父文件夹, 则返回 true, 否则返回 false
     */
    public boolean isParentDirectory(int userId, int directoryId, int fileId) {
        File directory = fileMapper.selectFileByFileId(userId, directoryId);
        File file = fileMapper.selectFileByFileId(userId, fileId);

        if (directory == null || !directory.isDirectory() || file == null) {
            return false;
        }

        while (file != null) {
            int parentDirectoryId = file.getDirectoryId();

            if (directoryId == parentDirectoryId) {
                return true;
            }

            // 遍历到了根目录
            if (File.ROOT_DIRECTORY_ID == parentDirectoryId) {
                return false;
            }

            file = fileMapper.selectFileByFileId(userId, parentDirectoryId);
        }

        return false;
    }
}
