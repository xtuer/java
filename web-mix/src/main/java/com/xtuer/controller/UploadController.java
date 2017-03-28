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
        System.out.println(file.getOriginalFilename());

        return "Success";
    }

    /**
     * CKEditor 上传图片
     *
     * @param file 上传的图片
     * @param funcNumber 图片上传成功后的回调标志
     * @return 返回一段 <script></script>，CKEditor 会调用这一段 script
     */
    @PostMapping("/ckeditor-upload-image")
    @ResponseBody
    public String ckeditorUploadImage(@RequestParam("upload") MultipartFile file, @RequestParam("CKEditorFuncNum") String funcNumber) {
        // [1] 存储上传得到的图片网络地址
        System.out.println("上传图片: " + file.getOriginalFilename());
        System.out.println("FuncNumber: " + funcNumber);

        // [2] 检查图片的格式，如果有错返回错误的 script，如 : <script>alert('图片格式不支持');</script>

        // [3] 上传成功，返回的 script 里带上图片的网络地址
        String imageUrl = "/img/x.png"; // 为了简单测试，总返回同一个图片地址
        return String.format("<script>window.parent.CKEDITOR.tools.callFunction(%s, '%s')</script>", funcNumber, imageUrl);
    }

    /**
     * CKEditor 上传文件
     *
     * @param file 上传的文件
     * @param funcNumber 文件上传成功后的回调标志
     * @return 返回一段 <script></script>，CKEditor 会调用这一段 script
     */
    @PostMapping("/ckeditor-upload-file")
    @ResponseBody
    public String ckeditorUploadFile(@RequestParam("upload") MultipartFile file, @RequestParam("CKEditorFuncNum") String funcNumber) {
        // [1] 存储上传得到的文件网络地址
        System.out.println("上传文件: " + file.getOriginalFilename());
        System.out.println("FuncNumber: " + funcNumber);

        // [2] 检查文件的格式，如果有错返回错误的 script，如 : <script>alert('文件格式不支持');</script>

        // [3] 上传成功，返回的 script 里带上图片的网络地址
        String fileUrl = "/files/x.rar"; // 为了简单测试，总返回同一个文件的地址
        return String.format("<script>window.parent.CKEDITOR.tools.callFunction(%s, '%s')</script>", funcNumber, fileUrl);
    }

    @PostMapping("/umeditor-upload")
    @ResponseBody
    public String uploadUmeditor() {
        return null;
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
