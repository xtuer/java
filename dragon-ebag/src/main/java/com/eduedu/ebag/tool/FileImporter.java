package com.eduedu.ebag.tool;

import com.eduedu.ebag.bean.Result;
import com.eduedu.ebag.bean.filesystem.FileInfo;
import com.eduedu.ebag.mapper.filesystem.FileMapper;
import com.eduedu.ebag.service.FileSystemService;
import com.eduedu.ebag.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 导入资源文件, 需要在 resources/tool/file-importer 中配置导入文件的目录, 用户的 id
 */
public class FileImporter {
    private static Logger logger = LoggerFactory.getLogger(FileImporter.class);
    private int userId;
    private String baseDirectory;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private FileMapper fileMapper;

    /**
     * 导入文件夹 dir 下面的所有文件到 parentDirectoryId 表示的文件夹下。
     * 导入的文件夹在 parentDirectoryId 中的目录结构结构和 dir 下的一样。
     *
     * @param file 要导入的文件所在的根目录
     * @param parentDirectoryId 要导入到的目录的 id
     * @throws Exception
     */
    public void importFiles(File file, int parentDirectoryId) throws Exception {
        if (!file.exists()) {
            logger.info("文件 '{}' 不存在", file.getAbsolutePath());
            return;
        }

        if (file.isDirectory()) {
            String directoryName = file.getName();
            int pid = findOrCreateDirectory(directoryName, parentDirectoryId);

            if (pid == -1) {
                return;
            }

            for (File f : file.listFiles()) {
                importFiles(f, pid);
            }
        } else {
            importFile(file, parentDirectoryId);
        }
    }

    /**
     * 查找父目录的 id, 如果不存在则创建新的目录, 并返回新目录的 id
     *
     * @param directoryName
     * @param parentDirectoryId
     * @return
     */
    private int findOrCreateDirectory(String directoryName, int parentDirectoryId) {
        List<Integer> ids = fileMapper.selectDirectoryIdsByDirectoryName(userId, directoryName, parentDirectoryId);

        if (ids.size() > 0) {
            return ids.get(0);
        }

        // 不存在则创建
        Result result = fileSystemService.createDirectory(userId, parentDirectoryId, directoryName);
        System.out.println(result.getMessage());

        if (result.isSuccess()) {
            return ((com.eduedu.ebag.bean.filesystem.File) result.getData()).getFileId();
        }

        return -1;
    }

    private void importFile(File file, int parentDirectoryId) throws Exception {
        String originalName = file.getName(); // 上传的文件的名字

        // 不导入 Linux 下的隐藏文件
        if (originalName.startsWith(".")) {
            return;
        }

        // 如果已经在同一个目录下, 就不在导入
        if (fileMapper.selectFileIdsByFileName(userId, originalName, parentDirectoryId).size() > 0) {
            logger.info("文件 '{}' 已经存在, 不再重复导入", file.getAbsolutePath());
            return;
        }

        long size = file.length(); // 上传的文件的大小
        InputStream in = new FileInputStream(file);

        FileInfo fileInfo = fileUploadService.saveToTemporaryDirectory(in, originalName, size);
        if (fileInfo != null) {
            fileUploadService.saveFileInformation(fileInfo, parentDirectoryId, userId);
            logger.info("成功导入文件 '{}'", file.getAbsolutePath());
        }
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("tool/file-importer.xml");
        FileImporter fileImporter = context.getBean("fileImporter", FileImporter.class);
        fileImporter.importFiles(new File(fileImporter.getBaseDirectory()), 0);
    }
}
