package com.eduedu.ebag.service;

import com.eduedu.ebag.bean.filesystem.File;
import com.eduedu.ebag.bean.filesystem.FileInfo;
import com.eduedu.ebag.mapper.filesystem.FileInfoMapper;
import com.eduedu.ebag.mapper.filesystem.FileMapper;
import com.eduedu.ebag.util.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class FileUploadService {
    private static Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    private String uploadTemporaryDirectory = "/dragon-ebag/upload-temp";
    private String uploadDirectory          = "/dragon-ebag/upload";

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private FileMapper fileMapper;

    /**
     * 保存文件到用户指定的目录下
     *
     * @param userId 用户的 id
     * @param directoryId 文件夹的 id
     * @param multipartFiles 要上传的文件
     * @return 上传成功的文件信息
     */
    public List<File> saveMultipartFiles(int userId, int directoryId, MultipartFile[] multipartFiles) {
        LinkedList<File> uploadedFiles = new LinkedList<File>();

        for (MultipartFile mpf : multipartFiles) {
            if (mpf != null && !mpf.isEmpty()) {
                try {
                    String originalName = mpf.getOriginalFilename(); // 上传的文件的名字
                    long size = mpf.getSize(); // 上传的文件的大小
                    InputStream in = mpf.getInputStream();

                    FileInfo fileInfo = saveToTemporaryDirectory(in, originalName, size);

                    if (fileInfo != null) {
                        uploadedFiles.add(saveFileInformation(fileInfo, directoryId, userId));
                    }
                } catch (IOException e) {
                    logger.info("文件上传失败: " + ExceptionUtils.getStackTrace(e));
                }
            }
        }

        return uploadedFiles;
    }

    /**
     * 保存文件的数据流到上传目录:
     *     1. 生成一个不会重复的唯一性名字
     *     2. 把文件流保存到临时目录'
     *     3. 计算临时文件的 MD5, 查看是否有文件的 MD5 与之相等
     *     4. 如果文件不存在, 则移动到上传目录, 如果文件存在, 则复用已经存在的文件
     *
     * @param in 文件数据的流
     * @param originalName 文件的源名字
     * @param size 文件的大小(bytes)
     * @return 保存成功返回文件信息的 FileInfo 对象, 否则返回 null
     */
    public FileInfo saveToTemporaryDirectory(InputStream in, String originalName, long size) {
        FileInfo info = null;

        try {
            // 1. 生成一个不会重复的唯一性名字
            String uniqueName   = CommonUtils.generateUniqueFileName(originalName);

            // 2. 把文件流保存到临时目录
            String temporaryFilePath = buildTemporaryFilePath(uniqueName);
            FileUtils.copyInputStreamToFile(in, new java.io.File(temporaryFilePath));
            logger.debug("保存文件到临时目录: {}, 源文件名: {}", temporaryFilePath, originalName);

            // 3. 计算临时文件的 MD5, 查看是否有文件的 MD5 与之相等
            String md5 = CommonUtils.fileMd5(temporaryFilePath);
            info = fileInfoMapper.selectFileInfoByMd5(md5);

            if (info == null) {
                // 4. 如果文件不存在, 则移动到上传目录
                String directory = CommonUtils.dateToString(new Date()); // 每天上传的文件存在当天的目录里
                java.io.File uploadDirectory = new java.io.File(buildUploadDirectory(directory));
                FileUtils.moveFileToDirectory(new java.io.File(temporaryFilePath), uploadDirectory, true);
                logger.debug("移动文件到上传目录: {}/{}, 源文件名: {}", uploadDirectory, uniqueName, originalName);

                // 创建文件信息
                info = new FileInfo(originalName, uniqueName, directory, size, md5, new Date());
            } else {
                // 4. 文件存在, 则复用
                logger.debug("文件已经存在, 不移动到上传目录, ID: {}, MD5: {}", info.getFileInfoId(), info.getMd5());
                info.setOriginalName(originalName); // 虽然文件的内容相同, 但是有可能文件名不同
            }
        } catch (IOException ex) {
            logger.info("文件上传失败: " + ExceptionUtils.getStackTrace(ex));
        }

        return info;
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
    public File saveFileInformation(FileInfo fileInfo, int directoryId, int userId) {
        // 1. 向表 file_info 中插入文件的信息
        FileInfo temp = fileInfoMapper.selectFileInfoByMd5(fileInfo.getMd5());

        if (temp == null) {
            fileInfoMapper.insertFileInfo(fileInfo);
        }

        // 2. 向表 file 中插入文件的信息
        String displayName = fileInfo.getOriginalName();
        String fileUniqueName = fileInfo.getUniqueName();
        File file = new File(displayName, fileUniqueName, directoryId, File.TYPE_FILE, userId);
        fileMapper.insertFile(file);

        return file;
    }

    /**
     * 创建文件在临时上传目录的路径
     *
     * @param fileName 文件名
     * @return 临时上传目录的路径
     */
    public String buildTemporaryFilePath(String fileName) {
        return uploadTemporaryDirectory + java.io.File.separator + fileName;
    }

    /**
     * 创建文件的上传目录的路径
     *
     * @param subdirectory 文件夹的名字
     * @return 文件的上传目录的路径
     */
    public String buildUploadDirectory(String subdirectory) {
        return uploadDirectory + java.io.File.separator + subdirectory;
    }
}
