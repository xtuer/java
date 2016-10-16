package com.xtuer.controller;

import com.xtuer.bean.FileMeta;
import com.xtuer.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

@Controller
public class UploadController {
    @Autowired
    private ServletContext servletContext;

    /**
     * 上传单个文件的页面
     * @return 页面的路径
     */
    @RequestMapping(value = "/upload-file", method = RequestMethod.GET)
    public String uploadFilePage() {
        return "upload-file.html";
    }

    /**
     * 上传单个文件
     *
     * @param file MultipartFile 的对象
     * @return 上传的结果
     * @throws IOException
     */
    @RequestMapping(value = "/upload-file", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        saveFile(file);

        return "Success";
    }

    /**
     * 上传多个文件的页面
     * @return 页面的路径
     */
    @RequestMapping(value = "/upload-files", method = RequestMethod.GET)
    public String uploadFilesPage() {
        return "upload-files.html";
    }

    /**
     * 上传多个文件
     *
     * @param files MultipartFile 的对象数组
     * @return 上传的结果
     * @throws IOException
     */
    @RequestMapping(value = "/upload-files", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFiles(@RequestParam("file") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            saveFile(file);
        }

        return "Success";
    }

    @RequestMapping(value = "/ajax-upload-files", method = RequestMethod.GET)
    public String ajaxUploadFilesPage() {
        return "ajax-upload-files.html";
    }

    @RequestMapping(value="/ajax-upload-files", method = RequestMethod.POST)
    @ResponseBody
    public LinkedList<FileMeta> ajaxUploadFiles(@RequestParam("files") MultipartFile[] files) throws IOException {
        LinkedList<FileMeta> uploadedFiles = new LinkedList<FileMeta>();

        for (MultipartFile file : files) {
            FileMeta fileMeta = new FileMeta();
            fileMeta.setFileName(file.getOriginalFilename());
            fileMeta.setFileSize(file.getSize()/1024 + " Kb");
            fileMeta.setFileType(file.getContentType());

            if (saveFile(file)) {
                uploadedFiles.add(fileMeta);
            }
        }

        // Result will be like this:
        // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"}, ...]
        return uploadedFiles;
    }

    /**
     * 把 HTTP 请求中的文件流保存到本地
     *
     * @param file MultipartFile 的对象
     */
    private boolean saveFile(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // getRealPath() 取得 WEB-INF 所在文件夹路径
                // 如果参数是 "/temp", 当 temp 存在时返回 temp 的本地路径, 不存在时返回 null/temp (无效路径)
                String path = servletContext.getRealPath("") + File.separator + file.getOriginalFilename();
                FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(path));

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @GetMapping("/crossDomain")
    public String crossDomainPage() {
        return "crossDomain.html";
    }

    @PostMapping("/crossDomain")
    @ResponseBody
    public Result crossDomainHandler(@RequestParam String username, @RequestParam String email, MultipartFile file,
                                     HttpServletResponse response) throws IOException {
        System.out.println(file.getOriginalFilename());
        file.transferTo(new File("/Users/Biao/Desktop/x.html"));

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST");
        String message = String.format("Username: %s, Email: %s", username, email);
        System.out.println(message);

        return new Result(true, message);
    }
}
