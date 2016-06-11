package com.eduedu.ebag.controller.filesystem;

import com.eduedu.ebag.bean.Result;
import com.eduedu.ebag.bean.filesystem.File;
import com.eduedu.ebag.controller.UriViewConstants;
import com.eduedu.ebag.mapper.filesystem.FileMapper;
import com.eduedu.ebag.service.FileSystemService;
import com.eduedu.ebag.util.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class FileSystemController {
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileSystemService fileSystemService;

    @RequestMapping(UriViewConstants.URI_FILE_SYSTEM)
    public String fileSystemPage() {
        return UriViewConstants.VIEW_FILE_SYSTEM;
    }

    /**
     * 列出目录下的文件和目录
     * @param directoryId
     * @return
     */
    @RequestMapping(value=UriViewConstants.REST_FILE_SYSTEM_FILES, method=RequestMethod.GET)
    @ResponseBody
    public List<File> listFilesByDirectoryId(@PathVariable int directoryId) {
        int userId = SecurityUtils.getLoginUserId();
        return fileMapper.listFilesByDirectoryId(userId, directoryId);
    }

    /**
     * 创建目录
     * @param map 如 [parentDirectoryId=0, directoryName=新建文件夹]
     */
    @RequestMapping(value=UriViewConstants.REST_FILE_SYSTEM_DIRECTORIES, method=RequestMethod.POST)
    @ResponseBody
    public Result createDirectory(@RequestBody Map<String, String> map) {
        int userId = SecurityUtils.getLoginUserId();
        int parentDirectoryId = NumberUtils.toInt(map.get("parentDirectoryId"));
        String directoryName = map.get("directoryName");

        return fileSystemService.createDirectory(userId, parentDirectoryId, directoryName);
    }

    /**
     * 修改文件的属性, 例如重命名
     *
     * @param fileId
     * @param map 如 [action=RENAME, displayName=新的文件名]
     * @return
     */
    @RequestMapping(value=UriViewConstants.REST_FILE_SYSTEM_FILES_WITH_ID, method=RequestMethod.PUT)
    @ResponseBody
    public Result updateFile(@PathVariable int fileId, @RequestBody Map<String, String> map) {
        int userId = SecurityUtils.getLoginUserId();
        String action = StringUtils.trim(map.get("action")).toLowerCase();

        switch (action) {
            case FileSystemService.ACTION_RENAME:
                String displayName = map.get("displayName");
                return fileSystemService.renameFile(userId, fileId, displayName);
            default:
                return new Result(false, "No such action: " + action);
        }
    }

    /**
     * 删除文件
     * @param fileId
     * @return
     */
    @RequestMapping(value=UriViewConstants.REST_FILE_SYSTEM_FILES_WITH_ID, method=RequestMethod.DELETE)
    @ResponseBody
    public Result deleteFile(@PathVariable int fileId) {
        int userId = SecurityUtils.getLoginUserId();
        return fileSystemService.deleteFile(userId, fileId);
    }
}
