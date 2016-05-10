package com.eduedu.ebag.service;

import com.eduedu.ebag.bean.filesystem.FileDirectory;
import com.eduedu.ebag.bean.filesystem.FileInfo;
import com.eduedu.ebag.mapper.filesystem.FileInfoMapper;
import com.eduedu.ebag.util.CommonUtils;
import com.eduedu.ebag.mapper.filesystem.FileDirectoryMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service
public class UploadService {
    private static Logger logger = LoggerFactory.getLogger(UploadService.class);
    private String uploadTemporaryDirectory = "/dragon-ebag/upload-temp";
    private String uploadDirectory          = "/dragon-ebag/upload";

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private FileDirectoryMapper fileDirectoryMapper;

    /**
     * 1. 把 HTTP 请求中的文件流保存到本地临时目录
     * 2. 计算并利用文件的 MD5 判断文件是否存在
     *      2.1 文件存在就使用已有的文件
     *      2.2 文件不存在则移动到上传目录
     * 3. 返回文件的信息
     *
     * @param file MultipartFile 的对象
     * @return 保存成功返回文件信息的 FileInfo 对象, 否则返回 null
     */
    public FileInfo saveFileToDisk(MultipartFile file) {
        FileInfo node = null;

        if (!file.isEmpty()) {
            try {
                // 取得文件原来的名字, 并为其生成一个不会重复的唯一性名字, 且每天上传的文件存在当天的目录里
                String originalName = file.getOriginalFilename();
                String uniqueName   = CommonUtils.generateUniqueFileName(originalName);
                String directory    = CommonUtils.dateToString(new Date());

                // 把 HTTP 请求中的文件流保存到本地临时目录
                String temporaryFilePath = buildTemporaryFilePath(uniqueName);
                FileUtils.copyInputStreamToFile(file.getInputStream(), new File(temporaryFilePath));
                logger.debug("上传文件到临时目录: {}, 源文件名: {}", temporaryFilePath, originalName);

                // 计算临时文件的 MD5, 查看是否有文件的 MD5 与之相等
                String md5 = CommonUtils.fileMd5(temporaryFilePath);
                node = fileInfoMapper.selectFileInfoByMd5(md5);

                if (node == null) {
                    // 文件不存在, 移动到上传目录
                    File uploadDirectory = new File(buildUploadDirectory(directory));
                    FileUtils.moveFileToDirectory(new File(temporaryFilePath), uploadDirectory, true);
                    logger.debug("移动文件到上传目录: {}/{}, 源文件名: {}", uploadDirectory, uniqueName, originalName);

                    // 创建文件信息
                    node = new FileInfo(originalName, uniqueName, directory, file.getSize(), md5, new Date());
                } else {
                    logger.debug("文件已经存在, 不移动到上传目录, ID: {}, MD5: {}", node.getFileNodeId(), node.getMd5());
                    node.setOriginalName(originalName); // 虽然文件的内容相同, 但是有可能文件名不同
                }
            } catch (IOException ex) {
                logger.info("文件上传失败: " + ExceptionUtils.getStackTrace(ex));
            }
        }

        return node;
    }

    /**
     * 保存文件信息到数据库中.
     *
     * 1. 向 file_info 中插入文件的信息
     * 2. 向 file_directory 中插入文件的信息
     *
     * @param fileInfo 文件信息对象
     * @param directoryId 文件所在文件夹的 id
     * @param userId 文件所属用户的 id
     * @return 保存成功返回 true, 否则返回 false
     */
    @Transactional
    public boolean saveFileInformation(FileInfo fileInfo, int directoryId, int userId) {
        // 1. 向 file_info 中插入文件的信息
        FileInfo temp = fileInfoMapper.selectFileInfoByMd5(fileInfo.getMd5());

        if (temp == null) {
            fileInfoMapper.insertFileInfo(fileInfo);
        }

        // 2. 向 file_directory 中插入文件的信息
        String displayName = fileInfo.getOriginalName();
        String fileUniqueName = fileInfo.getUniqueName();
        FileDirectory fileDirectory = new FileDirectory(displayName, fileUniqueName, true, directoryId, userId);
        fileDirectoryMapper.insertFileDirectory(fileDirectory);

        return true;
    }

    /**
     * 创建文件在临时上传目录的路径
     *
     * @param fileName 文件名
     * @return
     */
    public String buildTemporaryFilePath(String fileName) {
        return uploadTemporaryDirectory + File.separator + fileName;
    }

    public String buildUploadDirectory(String subdirectory) {
        return uploadDirectory + File.separator + subdirectory;
    }
}
