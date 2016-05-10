package com.eduedu.ebag.controller.filesystem;

import com.eduedu.ebag.bean.filesystem.FileMeta;
import com.eduedu.ebag.bean.filesystem.FileInfo;
import com.eduedu.ebag.service.UploadService;
import com.eduedu.ebag.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;

@Controller
public class UploadController {
    @Autowired
    private UploadService uploadService;

    @RequestMapping(value="/filesystem/ajax-upload-files-view", method=RequestMethod.GET)
    public String ajaxUploadFilesView() {
        return "ajax-upload-files-view.html";
    }

    /**
     *
     * @param files 上传文件 MultipartFile 的对象数组
     * @return 成功上传文件的信息, [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"}, ...]
     */
    @RequestMapping(value="/filesystem/common-file-upload/{directoryId}", method = RequestMethod.POST)
    @ResponseBody
    public LinkedList<FileMeta> ajaxUploadFiles(@RequestParam("files") MultipartFile[] files,
                                                @PathVariable("directoryId") int directoryId) {
        int userId = SecurityUtils.getLoginUserId();

        LinkedList<FileMeta> uploadedFiles = new LinkedList<FileMeta>();

        for (MultipartFile file : files) {
            FileInfo fileInfo = uploadService.saveFileToDisk(file);

            if (fileInfo != null && uploadService.saveFileInformation(fileInfo, directoryId, userId)) {
                FileMeta fileMeta = new FileMeta();
                fileMeta.setFileName(file.getOriginalFilename());
                fileMeta.setFileSize(file.getSize()/1024 + " Kb");
                fileMeta.setFileType(file.getContentType());

                uploadedFiles.add(fileMeta);
            }
        }

        return uploadedFiles;
    }
}
