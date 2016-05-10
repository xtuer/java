package com.eduedu.ebag.controller.filesystem;

import com.eduedu.ebag.bean.filesystem.FileDirectory;
import com.eduedu.ebag.mapper.filesystem.FileDirectoryMapper;
import com.eduedu.ebag.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FileSystemController {
    @Autowired
    private FileDirectoryMapper fileDirectoryMapper;

    /**
     * 列出文件夹下的文件, directoryId 是文件夹的 id
     * @return
     */
    @RequestMapping(value="/filesystem/file-directory/{directoryId}/files", method= RequestMethod.GET)
    @ResponseBody
    public List<FileDirectory> listFiles(@PathVariable int directoryId) {
        int userId = SecurityUtils.getLoginUserId();
        return fileDirectoryMapper.listFilesByDirectoryId(directoryId, userId);
    }

    /**
     * 列出文件夹下的子文件夹, directoryId 是文件夹的 id
     * @return
     */
    @RequestMapping(value="/filesystem/file-directory/{directoryId}/subdirectories", method= RequestMethod.GET)
    @ResponseBody
    public List<FileDirectory> listSubdirectories(@PathVariable int directoryId) {
        int userId = SecurityUtils.getLoginUserId();
        return fileDirectoryMapper.listSubdirectoriesByDirectoryId(directoryId, userId);
    }

    /**
     * 列出文件夹下的文件和子文件夹, directoryId 是文件夹的 id
     * @return
     */
    @RequestMapping(value="/filesystem/file-directory/{directoryId}/files-and-subdirectories", method= RequestMethod.GET)
    @ResponseBody
    public List<FileDirectory> listFilesAndSubdirectories(@PathVariable int directoryId) {
        int userId = SecurityUtils.getLoginUserId();
        return fileDirectoryMapper.listFilesAndSubdirectoriesByDirectoryId(directoryId, userId);
    }

    /**
     * 创建文件夹(文件是在上传的时候创建的)
     * @return
     */
    @RequestMapping(value="/filesystem/file-directory", method= RequestMethod.POST)
    @ResponseBody
    public String createDirectory(@PathVariable int fileDirectoryId) {
        return null;
    }

    /**
     * 获取文件或者文件夹信息
     * @return
     */
    @RequestMapping(value="/filesystem/file-directory/{fileDirectoryId}", method= RequestMethod.GET)
    @ResponseBody
    public String getFileDirectory(@PathVariable int fileDirectoryId) {
        return null;
    }

    /**
     * 重命名文件或者文件夹
     * @return
     */
    @RequestMapping(value="/filesystem/file-directory/{fileDirectoryId}", method= RequestMethod.PUT)
    @ResponseBody
    public String updateFileDirectory(@PathVariable int fileDirectoryId, @RequestParam String displayName) {
        return null;
    }

    /**
     * 删除文件或者文件夹
     * @return
     */
    @RequestMapping(value="/filesystem/file-directory/{fileDirectoryId}", method= RequestMethod.DELETE)
    @ResponseBody
    public String deleteFileDirectory(@PathVariable int fileDirectoryId) {
        return null;
    }
}
